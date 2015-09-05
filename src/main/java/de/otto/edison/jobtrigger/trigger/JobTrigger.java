package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.springframework.scheduling.Trigger;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class JobTrigger {

    private final JobDefinition definition;
    private final Trigger trigger;
    private final Runnable runnable;

    public JobTrigger(final JobDefinition definition,
                      final Trigger trigger,
                      final Runnable runnable) {
        this.definition = definition;
        this.trigger = trigger;
        this.runnable = runnable;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public JobDefinition getDefinition() {
        return definition;
    }
}
