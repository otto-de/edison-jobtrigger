package de.otto.edison.jobtrigger.security;

import org.asynchttpclient.BoundRequestBuilder;

public interface AuthHeaderProvider {

    void setAuthHeader(BoundRequestBuilder requestBuilder);
}
