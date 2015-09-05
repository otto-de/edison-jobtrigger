package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class DiscoveryService {

    private final DiscoveryStrategy strategy;

    public DiscoveryService() {
        this.strategy = new StaticDiscoveryStrategy(asList(
                new JobDefinition("Statically configured", "http://localhost:8080/jobtrigger/stubs/job/FullImport", "FullImport", "Once every minute", Optional.<String>empty(), Optional.of(Duration.ofMinutes(1))),
                new JobDefinition("Statically configured", "http://localhost:8080/jobtrigger/stubs/job/DeltaImport", "DeltaImport", "Once every second", Optional.<String>empty(), Optional.of(Duration.ofSeconds(1)))
        ));
    }

    public DiscoveryService(final DiscoveryStrategy strategy) {
        this.strategy = strategy;
    }

    public void rediscoverFrom(final String discoveryUrl) {
    }

    public List<JobDefinition> dicoveredJobDefinitions() {
        return strategy.discover();
    }
}
