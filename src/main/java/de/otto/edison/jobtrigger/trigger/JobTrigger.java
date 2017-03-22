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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobTrigger other = (JobTrigger) obj;
		if (definition == null) {
			if (other.definition != null)
				return false;
		} else if (!definition.equals(other.definition))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobTrigger [definition=" + definition + ", trigger=" + trigger + ", runnable=" + runnable + "]";
	}
    
    
}
