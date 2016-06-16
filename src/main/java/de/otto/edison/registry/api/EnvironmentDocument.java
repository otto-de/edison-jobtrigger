package de.otto.edison.registry.api;

import de.otto.edison.registry.service.RegisteredService;

import java.util.List;

import static de.otto.edison.registry.api.Link.link;
import static java.util.stream.Collectors.toList;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class EnvironmentDocument {

    private final List<String> groups;
    private final List<Link> links;

    public EnvironmentDocument(final List<RegisteredService> services,
                               final String env,
                               final String baseUrl) {
        this.groups = services
                .stream()
                .flatMap(s->s.getGroups().stream())
                .distinct()
                .collect(toList());
        this.links = services
                .stream()
                .map(s-> link("item", baseUrl + "/environments/" + env + "/" + s.getService(), s.getService()))
                .collect(toList());
        links.add(link("self", baseUrl + "/environments/" + env, "Self"));
        links.add(link("collection", baseUrl + "/environments", "All known environments"));
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

        EnvironmentDocument that = (EnvironmentDocument) o;

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
        return "EnvironmentDocument{" +
                "groups=" + groups +
                ", links=" + links +
                '}';
    }
}
