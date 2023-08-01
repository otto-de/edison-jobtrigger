package de.otto.edison.registry.api;

import com.google.gson.Gson;
import de.otto.edison.registry.service.RegisteredService;
import de.otto.edison.registry.service.Registry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static de.otto.edison.registry.api.UrlHelper.baseUriOf;
import static java.util.stream.Collectors.toList;

/**
 * The RegistryController is responsible to handle the registry of microservices.
 *
 * @author Guido Steinacker
 * @since 05.09.15
 */
@RestController
public class RegistryController {

    @Autowired
    private Registry registry;

    /**
     * Get the representation of all known environments.
     *
     * @return representation document
     */
    @RequestMapping(
            value = "/environments",
            method = RequestMethod.GET,
            produces = {"application/json", "application/vnd.otto.edison.links+json"}
    )
    public EnvironmentsDocument getEnvironments(final HttpServletRequest request) {

        final List<RegisteredService> services = registry.findServices();
        final List<String> environments = services
                .stream()
                .map(RegisteredService::getEnvironment)
                .distinct()
                .collect(toList());
        final List<String> groups = services
                .stream()
                .flatMap(s->s.getGroups().stream())
                .distinct()
                .collect(toList());
        return environmentsDocOf(environments, groups, request);
    }

    /**
     * Get the representation of a single environment.
     *
     * @return representation document
     */
    @RequestMapping(
            value = "/environments/{env}",
            method = RequestMethod.GET,
            produces = {"application/json", "application/vnd.otto.edison.links+json"}
    )
    public EnvironmentDocument getEnvironment(final @PathVariable String env,
                                              final @RequestParam(required = false) List<String> groups,
                                              final HttpServletRequest request) {

        final List<RegisteredService> selected = registry.findServices()
                .stream()
                .filter(service -> service.getEnvironment().equals(env))
                .filter(service -> groups == null || intersects(groups, service.getGroups()))
                .collect(toList());
        return environmentDocOf(selected, env, request);
    }

    /**
     * Get the representation of a single service.
     *
     * @return representation document
     */
    @RequestMapping(
            value = "/environments/{env}/{service}",
            method = RequestMethod.GET,
            produces = {"application/json", "application/vnd.otto.edison.links+json"}
    )
    public ServiceDocument getService(final @PathVariable String env,
                                      final @PathVariable String service,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response) throws IOException {

        final Optional<RegisteredService> selected = registry.findServices()
                .stream()
                .filter(s -> s.getEnvironment().equals(env))
                .filter(s -> s.getService().equals(service))
                .findAny();
        if (selected.isPresent()) {
            return serviceDocOf(selected.get(), request);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such service");
            return null;
        }
    }

    /**
     * Get the representation of a single service.
     *
     * @return representation document
     */
    @RequestMapping(
            value = "/environments/{env}/{service}",
            method = RequestMethod.PUT,
            consumes = {"application/json", "application/vnd.otto.edison.links+json"},
            produces = {"application/json", "application/vnd.otto.edison.links+json"}
    )
    public ServiceDocument putService(final @PathVariable String env,
                           final @PathVariable String service,
                           final @RequestBody String jsonBody,
                           final HttpServletRequest request,
                           final HttpServletResponse response) throws IOException {
        final ServiceDocument document = new Gson().fromJson(jsonBody, ServiceDocument.class);
        final Optional<Link> serviceLink = document.getLinks()
                .stream()
                .filter(link -> link.rel.equals("http://github.com/otto-de/edison/link-relations/microservice"))
                .findAny();
        if (serviceLink.isPresent()) {
            final RegisteredService registeredService = new RegisteredService(
                    service,
                    serviceLink.get().href,
                    serviceLink.get().title,
                    Duration.ofMinutes(document.getExpire()),
                    env,
                    document.getGroups()
            );
            registry.putService(registeredService);
            return serviceDocOf(registeredService, request);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    /**
     * Get the representation of a single service.
     *
     * @return representation document
     */
    @RequestMapping(
            value = "/environments/{env}/{service}",
            method = RequestMethod.DELETE
    )
    public void deleteService(final @PathVariable String env,
                                         final @PathVariable String service,
                                         final HttpServletRequest request,
                                         final HttpServletResponse response) throws IOException {

        registry.deleteService(env, service);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private EnvironmentsDocument environmentsDocOf(final List<String> environments,
                                                   final List<String> groups,
                                                   final HttpServletRequest request) {
        return new EnvironmentsDocument(environments, groups, baseUriOf(request));
    }

    private EnvironmentDocument environmentDocOf(final List<RegisteredService> services,
                                                 final String env,
                                                 final HttpServletRequest request) {
        return new EnvironmentDocument(services, env, baseUriOf(request));
    }

    private ServiceDocument serviceDocOf(final RegisteredService registeredService,
                                         final HttpServletRequest request) {
        return new ServiceDocument(registeredService, baseUriOf(request));
    }

    private boolean intersects(final List<String> one, final List<String> other) {
        for (final String s : one) {
            if (other.contains(s)) return true;
        }
        return false;
    }

}
