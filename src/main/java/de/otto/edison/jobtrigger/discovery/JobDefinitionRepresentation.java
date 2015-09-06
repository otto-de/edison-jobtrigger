package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.registry.api.Link;

import java.util.List;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class JobDefinitionRepresentation {
        private String type;
        private String name;
        private int retries;
        private Long  retryDelay;
        private String cron;
        private Long maxAge;
        private Long fixedDelay;
        private List<Link> links;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Long getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(Long fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "JobDefinitionRepresentation{" +
                "cron='" + cron + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", retries=" + retries +
                ", retryDelay=" + retryDelay +
                ", maxAge=" + maxAge +
                ", fixedDelay=" + fixedDelay +
                ", links=" + links +
                '}';
    }
}
