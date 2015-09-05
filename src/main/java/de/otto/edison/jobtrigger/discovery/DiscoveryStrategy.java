package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.jobtrigger.definition.JobDefinition;

import java.util.List;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public interface DiscoveryStrategy {
    List<JobDefinition> discover();
}
