package de.otto.edison.registry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.time.Duration.ofMinutes;
import static java.util.Arrays.asList;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
@Repository
public class Registry {

    private static final Logger LOG = LoggerFactory.getLogger(Registry.class);

    private final List<RegisteredService> services = new CopyOnWriteArrayList<>(asList(
            new RegisteredService("productreco", "http://example.org/ci/productreco", "ProductReco service", ofMinutes(1), "ci", asList("p13n")),
            new RegisteredService("productsearch", "http://example.org/ci/productsearch", "ProductSearch service", ofMinutes(1), "ci", asList("p13n", "san")),
            new RegisteredService("campaign", "http://example.org/ci/campaign", "Campaign service", ofMinutes(1), "ci", asList("p13n")),
            new RegisteredService("insights", "http://example.org/ci/insights", "Insights service", ofMinutes(5), "ci", asList("tesla")),
            new RegisteredService("productreco", "http://example.org/develop/productsearch", "ProductReco service", ofMinutes(1), "develop", asList("p13n")),
            new RegisteredService("productsearch", "http://example.org/develop/productsearch", "ProductSearch service", ofMinutes(1), "develop", asList("p13n", "san")),
            new RegisteredService("insights", "http://example.org/develop/insights", "ProductSearch service", ofMinutes(10), "develop", asList("tesla"))
    ));

    public List<RegisteredService> findServices() {
        services.removeIf(s -> {
            boolean expired = s.isExpired();
            if (expired) {
                LOG.info("Removing expired registry entry " + s.getHref());
            }
            return expired;
        });
        return services;
    }

    public void deleteService(final String env,
                              final String service) {
        services.removeIf(s->s.getEnvironment().equals(env) && s.getService().equals(service));
    }

    public void putService(final RegisteredService registeredService) {
        deleteService(registeredService.getEnvironment(), registeredService.getService());
        services.add(registeredService);
    }
}
