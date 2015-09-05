package de.otto.edison.jobtrigger.definition;

import java.time.Duration;
import java.util.Optional;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class JobDefinition {
    private final String discoverySource;
    private final String triggerUrl;
    private final String jobType;
    private final String description;
    private final Optional<String> cron;
    private Optional<Duration> fixedDelay;

    public JobDefinition(final String discoverySource,
                         final String triggerUrl,
                         final String jobType,
                         final String description,
                         final Optional<String> cron,
                         final Optional<Duration> fixedDelay) {
        this.discoverySource = discoverySource;
        this.triggerUrl = triggerUrl;
        this.jobType = jobType;
        this.description = description;
        this.cron = cron;
        this.fixedDelay = fixedDelay;
    }

    public String getDiscoverySource() {
        return discoverySource;
    }

    public String getJobType() {
        return jobType;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getCron() {
        return cron;
    }

    public String getTriggerUrl() {
        return triggerUrl;
    }

    public Optional<Duration> getFixedDelay() {
        return fixedDelay;
    }

}
