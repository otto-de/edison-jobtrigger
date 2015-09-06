package de.otto.edison.registry.api;

import de.otto.edison.registry.service.RegisteredService;

import java.util.ArrayList;
import java.util.List;

import static de.otto.edison.registry.api.Link.link;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class ServiceDocument {

    private List<String> groups;
    private long expire;
    private List<Link> links;

    public ServiceDocument() {

    }

    public ServiceDocument(final RegisteredService service,
                           final String baseUrl) {
        this.expire = service.getExpireAfter().toMinutes();
        this.groups = service.getGroups();
        this.links = new ArrayList<>();
        links.add(link("self", baseUrl + "/environments/" + service.getEnvironment() + "/" + service.getService(), "Self"));
        links.add(link("collection", baseUrl + "/environments/" + service.getEnvironment(), "All services in " + service.getEnvironment()));
        links.add(link("http://github.com/otto-de/edison/link-relations/microservice", service.getHref(), service.getDescription()));
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<Link> getLinks() {
        return links;
    }

    public long getExpire() {
        return expire;
    }
}
