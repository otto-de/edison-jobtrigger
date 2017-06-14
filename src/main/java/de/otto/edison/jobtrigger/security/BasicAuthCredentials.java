package de.otto.edison.jobtrigger.security;

import com.ning.http.util.Base64;
import de.otto.edison.jobtrigger.configuration.JobTriggerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableConfigurationProperties(JobTriggerProperties.class)
public class BasicAuthCredentials {

    public static String AUTHORIZATION_HEADER = "Authorization";

    private static final String BASIC_PREFIX = "Basic ";

    private final String basicAuthUser;
    private final String basicAuthPasswd;

    @Autowired
    public BasicAuthCredentials(final JobTriggerProperties jobTriggerProperties) {
        basicAuthUser = jobTriggerProperties.getSecurity().getBasicAuthUser();
        basicAuthPasswd = jobTriggerProperties.getSecurity().getBasicAuthPasswd();

    }

    public Optional<String> base64Encoded() {
        if (basicAuthUser == null || basicAuthPasswd == null) {
            return Optional.empty();
        } else {
            final String credentials = basicAuthUser + ":" + basicAuthPasswd;
            return Optional.of(BASIC_PREFIX + Base64.encode(credentials.getBytes()));
        }
    }
}
