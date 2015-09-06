package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class TriggerResult {
    private final String id;
    private final int statusCode;
    private final String location;
    private final JobDefinition jobDefinition;
    private final String time;

    public TriggerResult(String id, int statusCode, String location, JobDefinition jobDefinition) {
        this.id = id;
        this.statusCode = statusCode;
        this.location = location;
        this.jobDefinition = jobDefinition;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
    }

    public String getId() {
        return id;
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

    public String getTime() {
        return time;
    }

}
