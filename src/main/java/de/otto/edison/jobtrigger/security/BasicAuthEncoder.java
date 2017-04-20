package de.otto.edison.jobtrigger.security;

import com.ning.http.util.Base64;
import de.otto.edison.jobtrigger.configuration.JobTriggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BasicAuthEncoder {

    public static String AUTHORIZATION_HEADER = "Authorization";

    private static final String BASIC_PREFIX = "Basic ";
    private String encodedCredentials;

    @Autowired
    public BasicAuthEncoder(final JobTriggerProperties jobTriggerProperties) {
        final String basicAuthUser = jobTriggerProperties.getSecurity().getBasicAuthUser();
        final String basicAuthPasswd = jobTriggerProperties.getSecurity().getBasicAuthPasswd();

        if (basicAuthUser != null && basicAuthPasswd != null) {
            final String credentials = basicAuthUser + ":" + basicAuthPasswd;
            encodedCredentials = BASIC_PREFIX + Base64.encode(credentials.getBytes());
        }
    }

    public Optional<String> getEncodedCredentials() {
        return Optional.ofNullable(encodedCredentials);
    }
}
