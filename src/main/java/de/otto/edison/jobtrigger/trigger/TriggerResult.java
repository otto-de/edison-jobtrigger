package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.MEDIUM;

/**
 * Information about a triggered job.
 *
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class TriggerResult {
    private final String id;
    private final TriggerStatus status;
    private final Optional<String> location;
    private final JobDefinition jobDefinition;
    private final String time;

    public TriggerResult(final String id,
                         final TriggerStatus status,
                         final Optional<String> location,
                         final JobDefinition jobDefinition) {
        this.id = id;
        this.status = status;
        this.location = location;
        this.jobDefinition = jobDefinition;
        this.time = now().format(ofLocalizedDateTime(MEDIUM));
    }

    public String getId() {
        return id;
    }

    public JobDefinition getJobDefinition() {
        return jobDefinition;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public TriggerStatus getTriggerStatus() {
        return status;
    }

    public boolean failed() {
        return status.getState() == TriggerStatus.State.FAILED;
    }

    public String getTime() {
        return time;
    }

}
