package de.otto.edison.jobtrigger.discovery;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.security.AuthProvider;
import de.otto.edison.registry.api.Link;
import de.otto.edison.registry.service.RegisteredService;
import de.otto.edison.registry.service.Registry;
import jakarta.annotation.PostConstruct;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.symmetricDifference;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class DiscoveryService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryService.class);
    public static final String JOB_DEFINITION_LINK_RELATION_TYPE = "http://github.com/otto-de/edison/link-relations/job/definition";
    public static final String JOB_TRIGGER_LINK_RELATION_TYPE = "http://github.com/otto-de/edison/link-relations/job/trigger";

    private AsyncHttpClient httpClient;
    private Registry serviceRegistry;
    private AuthProvider authHeaderProvider;

    private volatile DiscoveryListener listener;
    private volatile ImmutableList<JobDefinition> jobDefinitions = ImmutableList.of();

    public DiscoveryService(AsyncHttpClient asyncHttpClient, Registry serviceRegistry, AuthProvider authHeaderProvider) {
        httpClient = asyncHttpClient;
        this.serviceRegistry = serviceRegistry;
        this.authHeaderProvider = authHeaderProvider;
    }

    @PostConstruct
    public void postConstruct() {
        newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::rediscover, 0, 1, MINUTES);
    }

    public void register(final DiscoveryListener listener) {
        this.listener = listener;
    }

    public void rediscover() {
        LOG.info("Starting rediscovery of job definitions...");
        final ImmutableList<JobDefinition> discoveryResult = discover();
        if (discoveryResult.size() != jobDefinitions.size() || !discoveryResult.containsAll(jobDefinitions)) {
            LOG.info("Discovered changes in job definitions. Old: " + jobDefinitions + " New: " + discoveryResult);
            LOG.info("Discovered changes in job definitions. Diff: " + symmetricDifference(copyOf(jobDefinitions), copyOf(discoveryResult)));
            jobDefinitions = discoveryResult;
            listener.updatedJobDefinitions();
        } else {
            LOG.info("No changes in job definitions");
        }
        LOG.info("...done");
    }

    public List<JobDefinition> allJobDefinitions() {
        return jobDefinitions;
    }

    private ImmutableList<JobDefinition> discover() {
        final List<JobDefinition> result = new CopyOnWriteArrayList<>();
        serviceRegistry.findServices()
                .parallelStream()
                .forEach(service -> {
                    final String jobDefinitionsUrl = service.getHref() + "/internal/jobdefinitions";
                    try {
                        LOG.info("Trying to find job definitions at " + jobDefinitionsUrl);
                        final BoundRequestBuilder boundRequestBuilder = httpClient
                                .prepareGet(jobDefinitionsUrl);
                        authHeaderProvider.setAuthHeader(boundRequestBuilder);
                        final Response response = boundRequestBuilder
                                .setHeader("Accept", "application/json")
                                .execute().get();
                        if (response.getStatusCode() < 300) {
                            result.addAll(jobDefinitionsFrom(service, response));
                        } else {
                            LOG.info("No definitions found for {}. Status={}", service.getService(), response.getStatusCode());
                        }
                    } catch (final Exception e) {
                        LOG.warn("Did not get a response from {} with Exception message '{}'.", jobDefinitionsUrl, e.getMessage(), e);
                    }
                });
        return ImmutableList.copyOf(result);
    }

    @VisibleForTesting
    List<JobDefinition> jobDefinitionsFrom(final RegisteredService service, final Response jobDefinitionsResponse) {
        final LinksRepresentation document = new Gson()
                .fromJson(jobDefinitionsResponse.getResponseBody(), LinksRepresentation.class);
        final List<String> jobDefinitionUrls = document.getLinks().stream()
                .filter(l -> l.rel.equals(JOB_DEFINITION_LINK_RELATION_TYPE))
                .map(l -> l.href)
                .collect(toList());
        if (jobDefinitionUrls.isEmpty()) {
            LOG.warn("Did not find any URLs with rel={}", JOB_DEFINITION_LINK_RELATION_TYPE);
        }
        final List<JobDefinition> jobDefinitions = new CopyOnWriteArrayList<>();
        jobDefinitionUrls
                .stream()
                .forEach(definitionUrl -> {
                    try {
                        LOG.info("Getting job definition from " + definitionUrl);
                        final BoundRequestBuilder boundRequestBuilder = httpClient
                                .prepareGet(definitionUrl);
                        authHeaderProvider.setAuthHeader(boundRequestBuilder);
                        final Response response = boundRequestBuilder
                                .setHeader("Accept", "application/json")
                                .execute().get();
                        if (response.getStatusCode() < 300) {
                            jobDefinitions.add(jobDefinitionFrom(definitionUrl, service, response));
                        } else {
                            LOG.info("Failed to get job definition with " + response.getStatusCode());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("Did not get a job definition from {}: {}", definitionUrl, e.getMessage());
                    }
                });
        LOG.info("Found " + jobDefinitions.size() + " job definitions.");
        return jobDefinitions;
    }

    @VisibleForTesting
    JobDefinition jobDefinitionFrom(final String definitionUrl, final RegisteredService service, final Response response) {
        final JobDefinitionRepresentation def = new Gson()
                .fromJson(response.getResponseBody(), JobDefinitionRepresentation.class);
        final Optional<Link> triggerLink = def.getLinks().stream()
                .filter(l -> l.rel.equals(JOB_TRIGGER_LINK_RELATION_TYPE))
                .findAny();
        if (triggerLink.isPresent()) {
            return new JobDefinition(
                    definitionUrl,
                    service.getEnvironment(),
                    service.getService(),
                    triggerLink.get().href,
                    def.getType(),
                    def.getName(),
                    ofNullable(def.getCron()),
                    ofNullable(def.getFixedDelay() != null ? ofSeconds(def.getFixedDelay()) : null),
                    def.getRetries(),
                    ofNullable(def.getRetryDelay() != null ? ofSeconds(def.getRetryDelay()) : null)
            );
        } else {
            LOG.warn("No link to job trigger found: " + def);
            return null;
        }
    }
}
