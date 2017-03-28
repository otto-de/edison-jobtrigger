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

    public Jobresults getJobresults() {
        return jobresults;
    }

    public void setJobresults(Jobresults jobresults) {
        this.jobresults = jobresults;
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

}
