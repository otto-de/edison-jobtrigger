package de.otto.edison.jobtrigger.security;

import org.asynchttpclient.BoundRequestBuilder;

public interface AuthProvider {

    void setAuthHeader(BoundRequestBuilder requestBuilder);
}
