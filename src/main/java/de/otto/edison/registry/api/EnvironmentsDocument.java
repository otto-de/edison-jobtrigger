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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnvironmentsDocument that = (EnvironmentsDocument) o;

        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        return links != null ? links.equals(that.links) : that.links == null;

    }

    @Override
    public int hashCode() {
        int result = groups != null ? groups.hashCode() : 0;
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnvironmentsDocument{" +
                "groups=" + groups +
                ", links=" + links +
                '}';
    }
}
