package de.otto.edison.jobtrigger.trigger;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.discovery.DiscoveryListener;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static de.otto.edison.jobtrigger.trigger.TriggerRunnables.httpTriggerRunnable;
import static de.otto.edison.jobtrigger.trigger.TriggerStatus.fromHttpStatus;
import static de.otto.edison.jobtrigger.trigger.TriggerStatus.fromMessage;
import static de.otto.edison.jobtrigger.trigger.Triggers.periodicTrigger;
import static java.lang.String.valueOf;
import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class TriggerService implements DiscoveryListener {

    private static final Logger LOG = getLogger(TriggerService.class);

    private DiscoveryService discoveryService;
    private JobScheduler scheduler;
    private AsyncHttpClient httpClient;

    private int maxJobResults = 1000;

    private final Deque<TriggerResult> lastResults = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final AtomicLong currentIndex = new AtomicLong(0);

    TriggerService() {
        // FOR TESTING
    }


    @Autowired
    public TriggerService(DiscoveryService discoveryService, JobScheduler scheduler, AsyncHttpClient httpClient, @Value("${edison.jobtrigger.jobresults.max:1000}") int maxJobResults) {
        this.discoveryService = discoveryService;
        this.scheduler = scheduler;
        this.httpClient = httpClient;
        this.maxJobResults = maxJobResults;
    }

    @PostConstruct
    public void postConstruct() {
        discoveryService.register(this);
    }

    public void startTriggering() {
        if (isStarted()) {
            stopTriggering();
        }
        final List<JobDefinition> jobDefinitions = discoveryService.allJobDefinitions();
        scheduler.updateTriggers(jobDefinitions
                .stream()
                .filter(jobDefinition -> jobDefinition.getFixedDelay().isPresent() || jobDefinition.getCron().isPresent())
                .map(toJobTrigger())
                .collect(toList()));
        isStarted.set(true);
    }

    public void stopTriggering() {
        scheduler.stopAllTriggers();
        isStarted.set(false);
    }

    @Override
    public void updatedJobDefinitions() {
        startTriggering();
    }

    public boolean isStarted() {
        return isStarted.get();
    }

    public List<TriggerResult> getLastResults() {
        return new ArrayList<>(lastResults);
    }

    private Runnable runnableFor(final JobDefinition jobDefinition) {
        return httpTriggerRunnable(httpClient, jobDefinition, new DefaultTriggerResponseConsumer(jobDefinition));
    }

    private Function<JobDefinition, JobTrigger> toJobTrigger() {
        return jobDefinition -> {
            try {
                return new JobTrigger(jobDefinition, triggerFor(jobDefinition), runnableFor(jobDefinition));
            } catch (final Exception e) {
                final Runnable failingJobRunnable = () -> {
                    lastResults.addFirst(new TriggerResult(nextId(), fromMessage(e.getMessage()), emptyMessage(), jobDefinition));
                };
                return new JobTrigger(jobDefinition, periodicTrigger(of(10, MINUTES)), failingJobRunnable);
            }
        };
    }

    private Optional<String> emptyMessage() {
        return Optional.<String>empty();
    }

    private String nextId() {
        return valueOf(currentIndex.addAndGet(1));
    }

    private Trigger triggerFor(final JobDefinition jobDefinition) {
        if (jobDefinition.getFixedDelay().isPresent()) {
            return periodicTrigger(jobDefinition.getFixedDelay().get());
        } else if (jobDefinition.getCron().isPresent()) {
            return Triggers.cronTrigger(jobDefinition.getCron().get());
        } else {
            LOG.warn("No Trigger found for job definition " + jobDefinition.getTriggerUrl());
            return triggerContext -> null;
        }
    }

    class DefaultTriggerResponseConsumer implements TriggerResponseConsumer {
        private final JobDefinition jobDefinition;

        public DefaultTriggerResponseConsumer(JobDefinition jobDefinition) {
            this.jobDefinition = jobDefinition;
        }

        @Override
        public void consume(final Response response) {
            try {
                int statusCode = response.getStatusCode();
                String location = response.getHeader("Location");
                lastResults.addFirst(new TriggerResult(nextId(), fromHttpStatus(statusCode), ofNullable(location), jobDefinition));
                while (lastResults.size() > maxJobResults) {
                    lastResults.removeLast();
                }
                LOG.info("Triggered {}: status = {}, location = {}", jobDefinition.getTriggerUrl(), statusCode, location);
            } catch (final Exception e) {
                LOG.error("Failed to trigger {}. Error: {}", jobDefinition.getTriggerUrl(), e.getMessage(), e);
            }
        }

        @Override
        public void consume(final Throwable throwable) {
            if (throwable instanceof ConnectException) {
                lastResults.addFirst(new TriggerResult(nextId(), fromMessage("Connection Refused"), emptyMessage(), jobDefinition));
            } else {
                lastResults.addFirst(new TriggerResult(nextId(), fromMessage(throwable.getMessage()), emptyMessage(), jobDefinition));
            }
            LOG.warn("Failed to trigger {}. Error: {}", jobDefinition.getTriggerUrl(), throwable.getMessage(), throwable);
        }
    }

    public int getMaxJobResults() {
        return maxJobResults;
    }
}
