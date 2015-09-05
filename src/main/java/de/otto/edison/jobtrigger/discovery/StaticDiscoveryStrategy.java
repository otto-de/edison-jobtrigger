package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.jobtrigger.definition.JobDefinition;

import java.util.List;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
public class StaticDiscoveryStrategy implements DiscoveryStrategy {

    private final List<JobDefinition> definitions;

    public StaticDiscoveryStrategy(final List<JobDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public List<JobDefinition> discover() {
        return definitions;
    }
}
