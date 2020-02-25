package de.otto.edison.registry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
@Repository
public class Registry {

    private static final Logger LOG = LoggerFactory.getLogger(Registry.class);

    private final List<RegisteredService> services = Collections.synchronizedList(new ArrayList<>());

    public List<RegisteredService> findServices() {
       services.removeIf(s -> {
            boolean expired = s.isExpired();
            if (expired) {
                LOG.info("Removing expired registry entry " + s.getHref());
            }
            return expired;
        });
        return new ArrayList<>(services);
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
