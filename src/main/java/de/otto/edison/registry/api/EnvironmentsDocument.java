package de.otto.edison.registry.api;

import java.util.List;

import static de.otto.edison.registry.api.Link.link;
import static java.util.stream.Collectors.toList;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class EnvironmentsDocument {

    private final List<String> groups;
    private final List<Link> links;

    public EnvironmentsDocument(final List<String> environments,
                                final List<String> groups,
                                final String baseUrl) {
        this.groups = groups;
        this.links = environments.stream().map(e-> link("item", baseUrl + "/environments/" + e, e)).collect(toList());
        links.add(link("self", baseUrl + "/environments", "Self"));
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<Link> getLinks() {
        return links;
    }
}
