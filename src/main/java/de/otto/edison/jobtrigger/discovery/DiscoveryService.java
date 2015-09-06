package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Service
public class DiscoveryService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryService.class);
    private static final long TEN_MINUTES = 10 * 60 * 1000L;

    @Autowired
    private DiscoveryStrategy strategy;
    private volatile List<JobDefinition> discoveredJobDefinitions = new CopyOnWriteArrayList<>();

    public DiscoveryService() {
    }

    public DiscoveryService(final DiscoveryStrategy strategy) {
        this.strategy = strategy;
    }

    @Scheduled(fixedDelay = TEN_MINUTES)
    public void rediscover() {
        LOG.info("Starting rediscovery of job definitions...");
        discoveredJobDefinitions = this.strategy.discover();
        LOG.info("...done");
    }

    public List<JobDefinition> dicoveredJobDefinitions() {
        return discoveredJobDefinitions;
    }
}
