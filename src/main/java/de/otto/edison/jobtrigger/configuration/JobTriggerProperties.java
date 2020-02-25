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
    }

}
