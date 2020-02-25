package de.otto.edison.jobtrigger.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties(prefix = "edison.jobtrigger")
public class JobTriggerProperties {

    @Valid
    private Jobresults jobresults = new Jobresults();

    @Valid
    private Scheduler scheduler = new Scheduler();

    @Valid
    private Security security = new Security();


    public Jobresults getJobresults() {
        return jobresults;
    }

    public void setJobresults(Jobresults jobresults) {
        this.jobresults = jobresults;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static class Jobresults {

        @Min(0)
        private int max = 1000;

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

    public static class Scheduler {

        @Min(1)
        private int poolsize = 10;

        public int getPoolsize() {
            return poolsize;
        }

        public void setPoolsize(int poolsize) {
            this.poolsize = poolsize;
        }
    }

    public static class Security {
        private AuthMethod authMethod = AuthMethod.BASIC;

        private OAuth2 oAuth2;

        private String basicAuthUser;

        private String basicAuthPasswd;

        public String getBasicAuthUser() {
            return basicAuthUser;
        }

        public String getBasicAuthPasswd() {
            return basicAuthPasswd;
        }

        public void setBasicAuthUser(String basicAuthUser) {
            this.basicAuthUser = basicAuthUser;
        }

        public void setBasicAuthPasswd(String basicAuthPasswd) {
            this.basicAuthPasswd = basicAuthPasswd;
        }

        public AuthMethod getAuthMethod() {
            return authMethod;
        }

        public void setAuthMethod(AuthMethod authMethod) {
            this.authMethod = authMethod;
        }

        public OAuth2 getoAuth2() {
            return oAuth2;
        }

        public void setoAuth2(OAuth2 oAuth2) {
            this.oAuth2 = oAuth2;
        }
    }

    public static class OAuth2 {
        private String clientId;
        private String clientSecret;
        private String tokenEndpoint;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public void setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
        }
    }

    public enum AuthMethod {
        BASIC, OAUTH2
    }
}
