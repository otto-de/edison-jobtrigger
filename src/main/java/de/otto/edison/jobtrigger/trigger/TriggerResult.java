package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class TriggerResult {
    private final int statusCode;
    private final String location;
    private final JobDefinition jobDefinition;

    public TriggerResult(int statusCode, String location, JobDefinition jobDefinition) {

        this.statusCode = statusCode;
        this.location = location;
        this.jobDefinition = jobDefinition;
    }

    public JobDefinition getJobDefinition() {
        return jobDefinition;
    }

    public String getLocation() {
        return location;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
