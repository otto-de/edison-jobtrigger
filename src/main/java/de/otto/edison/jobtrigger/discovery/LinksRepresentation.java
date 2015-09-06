package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.registry.api.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Guido Steinacker
 * @since 06.09.15
 */
public class LinksRepresentation {

    private List<Link> links = new ArrayList<>();

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Link> getLinks() {
        return links;
    }
}
