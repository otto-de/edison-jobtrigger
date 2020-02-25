package de.otto.edison.jobtrigger.security;

import com.ning.http.client.AsyncHttpClient;

public interface AuthHeaderProvider {

    void setAuthHeader(AsyncHttpClient.BoundRequestBuilder requestBuilder);
}
