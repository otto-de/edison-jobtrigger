package de.otto.edison.jobtrigger.definition;

import java.time.Duration;
import java.util.Optional;

public class JobDefinitionBuilder {
    private String definitionUrl;
    private String env;
    private String service;
    private String triggerUrl;
    private String jobType;
    private String description;
    private Optional<String> cron = Optional.empty();
    private Optional<Duration> fixedDelay = Optional.empty();
    private Optional<Duration> retryDelay = Optional.empty();
    private int retries;

    public JobDefinitionBuilder setDefinitionUrl(String definitionUrl) {
        this.definitionUrl = definitionUrl;
        return this;
    }

    public JobDefinitionBuilder setEnv(String env) {
        this.env = env;
        return this;
    }

    public JobDefinitionBuilder setService(String service) {
        this.service = service;
        return this;
    }

    public JobDefinitionBuilder setTriggerUrl(String triggerUrl) {
        this.triggerUrl = triggerUrl;
        return this;
    }

    public JobDefinitionBuilder setJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public JobDefinitionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JobDefinitionBuilder setCron(Optional<String> cron) {
        this.cron = cron;
        return this;
    }

    public JobDefinitionBuilder setFixedDelay(Optional<Duration> fixedDelay) {
        this.fixedDelay = fixedDelay;
        return this;
    }

    public JobDefinitionBuilder setRetries(int retries) {
        this.retries = retries;
        return this;
    }

    public JobDefinitionBuilder setRetryDelay(Optional<Duration> retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    public JobDefinition createJobDefinition() {
        return new JobDefinition(definitionUrl, env, service, triggerUrl, jobType, description, cron, fixedDelay, retries, retryDelay);
    }
}