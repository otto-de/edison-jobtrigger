package de.otto.edison.jobtrigger.security;

import java.util.Base64;
import de.otto.edison.jobtrigger.configuration.JobTriggerProperties;
import org.asynchttpclient.AsyncHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;

import java.util.Optional;

@Component
@EnableConfigurationProperties(JobTriggerProperties.class)
public class BasicAuthCredentials {

    private static final String VAULT_PREFIX = "VAULT ";
    public static String AUTHORIZATION_HEADER = "Authorization";

    private static final String BASIC_PREFIX = "Basic ";

    private final String basicAuthUser;
    private final String basicAuthPasswd;

    @Autowired
    public BasicAuthCredentials(final JobTriggerProperties jobTriggerProperties, final VaultOperations vaultOperations) {
        basicAuthUser = jobTriggerProperties.getSecurity().getBasicAuthUser();
        String passwd = jobTriggerProperties.getSecurity().getBasicAuthPasswd();

        if(passwd != null && passwd.startsWith(VAULT_PREFIX)) {
            String vaultPath = passwd.substring(VAULT_PREFIX.length());
            basicAuthPasswd = vaultOperations.read(vaultPath).getData().get("value").toString();
        } else {
            basicAuthPasswd = passwd;
        }
    }

    public Optional<String> base64Encoded() {
        if (basicAuthUser == null || basicAuthPasswd == null) {
            return Optional.empty();
        } else {
            final String credentials = basicAuthUser + ":" + basicAuthPasswd;
            return Optional.of(BASIC_PREFIX + Base64.getEncoder().encodeToString(credentials.getBytes()));
        }
    }
}
