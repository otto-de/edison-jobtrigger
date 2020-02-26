package de.otto.edison.jobtrigger.security;

import org.asynchttpclient.BoundRequestBuilder;

import static de.otto.edison.jobtrigger.security.BasicAuthCredentials.AUTHORIZATION_HEADER;

public class BasicAuthHeaderProvider implements AuthProvider {

    private final BasicAuthCredentials basicAuthCredentials;

    public BasicAuthHeaderProvider(BasicAuthCredentials basicAuthCredentials) {
        this.basicAuthCredentials = basicAuthCredentials;
    }

    @Override
    public void setAuthHeader(BoundRequestBuilder requestBuilder) {
        basicAuthCredentials.base64Encoded().ifPresent(encodedCredentials ->
                requestBuilder.setHeader(AUTHORIZATION_HEADER, encodedCredentials)
        );

    }
}
