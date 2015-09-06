package de.otto.edison.jobtrigger.definition;

import java.time.Duration;
import java.util.Optional;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class JobDefinition {
    private final String definitionUrl;
    private final String env;
    private final String service;
    private final String triggerUrl;
    private final String jobType;
    private final String description;
    private final Optional<String> cron;
    private Optional<Duration> fixedDelay;

    public JobDefinition(final String definitionUrl,
                         final String env,
                         final String service,
                         final String triggerUrl,
                         final String jobType,
                         final String description,
                         final Optional<String> cron,
                         final Optional<Duration> fixedDelay) {
        this.definitionUrl = definitionUrl;
        this.env = env;
        this.service = service;
        this.triggerUrl = triggerUrl;
        this.jobType = jobType;
        this.description = description;
        this.cron = cron;
        this.fixedDelay = fixedDelay;
    }

    public String getDefinitionUrl() {
        return definitionUrl;
    }

    public String getJobType() {
        return jobType;
    }

    public String getEnv() {
        return env;
    }

    public String getService() {
        return service;
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
