package de.otto.edison.jobtrigger.security;

import com.ning.http.client.AsyncHttpClient;
import org.springframework.stereotype.Component;

import static de.otto.edison.jobtrigger.security.BasicAuthCredentials.AUTHORIZATION_HEADER;

@Component
public class BasicAuthHeaderProvider implements AuthHeaderProvider {

    private final BasicAuthCredentials basicAuthCredentials;

    public BasicAuthHeaderProvider(BasicAuthCredentials basicAuthCredentials) {
        this.basicAuthCredentials = basicAuthCredentials;
    }

    @Override
    public void setAuthHeader(AsyncHttpClient.BoundRequestBuilder requestBuilder) {
        basicAuthCredentials.base64Encoded().ifPresent(encodedCredentials ->
                requestBuilder.setHeader(AUTHORIZATION_HEADER, encodedCredentials)
        );

    }
}
