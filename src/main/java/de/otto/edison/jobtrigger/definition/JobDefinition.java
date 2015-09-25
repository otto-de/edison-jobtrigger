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
    final int retries;
    final Optional<Duration> retryDelay;

    public JobDefinition(final String definitionUrl,
                         final String env,
                         final String service,
                         final String triggerUrl,
                         final String jobType,
                         final String description,
                         final Optional<String> cron,
                         final Optional<Duration> fixedDelay,
                         final int retries,
                         final Optional<Duration> retryDelay) {
        this.definitionUrl = definitionUrl;
        this.env = env;
        this.service = service;
        this.triggerUrl = triggerUrl;
        this.jobType = jobType;
        this.description = description;
        this.cron = cron;
        this.fixedDelay = fixedDelay;
        this.retries = retries;
        this.retryDelay = retryDelay;
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

    public int getRetries() {
        return retries;
    }

    public Optional<Duration> getRetryDelay() {
        return retryDelay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobDefinition that = (JobDefinition) o;

        if (retries != that.retries) return false;
        if (cron != null ? !cron.equals(that.cron) : that.cron != null) return false;
        if (definitionUrl != null ? !definitionUrl.equals(that.definitionUrl) : that.definitionUrl != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (env != null ? !env.equals(that.env) : that.env != null) return false;
        if (fixedDelay != null ? !fixedDelay.equals(that.fixedDelay) : that.fixedDelay != null) return false;
        if (jobType != null ? !jobType.equals(that.jobType) : that.jobType != null) return false;
        if (retryDelay != null ? !retryDelay.equals(that.retryDelay) : that.retryDelay != null) return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (triggerUrl != null ? !triggerUrl.equals(that.triggerUrl) : that.triggerUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = definitionUrl != null ? definitionUrl.hashCode() : 0;
        result = 31 * result + (env != null ? env.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (triggerUrl != null ? triggerUrl.hashCode() : 0);
        result = 31 * result + (jobType != null ? jobType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cron != null ? cron.hashCode() : 0);
        result = 31 * result + (fixedDelay != null ? fixedDelay.hashCode() : 0);
        result = 31 * result + retries;
        result = 31 * result + (retryDelay != null ? retryDelay.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobDefinition{" +
                "cron=" + cron +
                ", definitionUrl='" + definitionUrl + '\'' +
                ", env='" + env + '\'' +
                ", service='" + service + '\'' +
                ", triggerUrl='" + triggerUrl + '\'' +
                ", jobType='" + jobType + '\'' +
                ", description='" + description + '\'' +
                ", fixedDelay=" + fixedDelay +
                ", retries=" + retries +
                ", retryDelay=" + retryDelay +
                '}';
    }
}
