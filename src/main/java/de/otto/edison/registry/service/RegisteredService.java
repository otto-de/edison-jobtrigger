package de.otto.edison.registry.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Information about a registered service.
 *
 * @author Guido Steinacker
 * @since 06.09.15
 */
public final class RegisteredService {

    private final String service;
    private final String href;
    private final String description;
    private final Duration expireAfter;
    private final String environment;
    private final List<String> groups;
    private Instant lastUpdated;

    public RegisteredService(final String service,
                             final String href,
                             final String description,
                             final Duration expireAfter,
                             final String environment,
                             final List<String> groups) {
        this.href = href;
        this.description = description;
        this.environment = environment;
        this.groups = groups;
        this.service = service;
        this.expireAfter = expireAfter;
        this.lastUpdated = Instant.now();
    }

    private RegisteredService(Builder builder) {
        service = builder.service;
        href = builder.href;
        description = builder.description;
        expireAfter = builder.expireAfter;
        environment = builder.environment;
        groups = builder.groups;
        lastUpdated = builder.lastUpdated;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getService() {
        return service;
    }

    public String getDescription() {
        return description;
    }

    public Duration getExpireAfter() {
        return expireAfter;
    }

    public String getHref() {
        return href;
    }

    public String getEnvironment() {
        return environment;
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean isExpired() {
        return lastUpdated.plus(expireAfter).isBefore(Instant.now());
    }


    public static final class Builder {
        private String service;
        private String href;
        private String description;
        private Duration expireAfter;
        private String environment;
        private List<String> groups;
        private Instant lastUpdated;

        private Builder() {
        }

        public Builder withService(String val) {
            service = val;
            return this;
        }

        public Builder withHref(String val) {
            href = val;
            return this;
        }

        public Builder withDescription(String val) {
            description = val;
            return this;
        }

        public Builder withExpireAfter(Duration val) {
            expireAfter = val;
            return this;
        }

        public Builder withEnvironment(String val) {
            environment = val;
            return this;
        }

        public Builder withGroups(List<String> val) {
            groups = val;
            return this;
        }

        public Builder withLastUpdated(Instant val) {
            lastUpdated = val;
            return this;
        }

        public RegisteredService build() {
            return new RegisteredService(this);
        }
    }
}
