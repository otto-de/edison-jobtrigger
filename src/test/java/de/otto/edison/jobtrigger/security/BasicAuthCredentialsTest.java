package de.otto.edison.jobtrigger.security;

import de.otto.edison.jobtrigger.configuration.JobTriggerProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthCredentialsTest {

    @Mock
    private JobTriggerProperties jobTriggerProperties;

    @Mock
    private JobTriggerProperties.Security security;

    @Mock
    private VaultOperations vaultOperations;

    @Before
    public void setUp() throws Exception {
        when(security.getBasicAuthUser()).thenReturn("someUser");
        when(jobTriggerProperties.getSecurity()).thenReturn(security);
    }

    @Test
    public void shouldGetCredentialsFromProperties() throws Exception {
        //given
        when(security.getBasicAuthPasswd()).thenReturn("somePassword");

        //when
        BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(jobTriggerProperties, vaultOperations);

        Optional<String> credentials = basicAuthCredentials.base64Encoded();

        //then
        assertThat(credentials.orElse(null), is("Basic c29tZVVzZXI6c29tZVBhc3N3b3Jk"));
    }

    @Test
    public void shouldGetCredentialsFromVault() throws Exception {
        //given
        when(security.getBasicAuthPasswd()).thenReturn("VAULT some/path/in/vault");

        VaultResponse vaultResponse = mock(VaultResponse.class);
        when(vaultOperations.read("some/path/in/vault")).thenReturn(vaultResponse);
        when(vaultResponse.getData()).thenReturn(Collections.singletonMap("value", "somePassword"));

        //when
        BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(jobTriggerProperties, vaultOperations);

        Optional<String> credentials = basicAuthCredentials.base64Encoded();

        //then
        assertThat(credentials.orElse(null), is("Basic c29tZVVzZXI6c29tZVBhc3N3b3Jk"));
    }

    @Test
    public void shouldReturnEmptyIfNoCredentialsAreSet() throws Exception {
        when(security.getBasicAuthPasswd()).thenReturn(null);

        //when
        BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(jobTriggerProperties, vaultOperations);

        Optional<String> credentials = basicAuthCredentials.base64Encoded();

        //then
        assertThat(credentials.isPresent(), is(false));
    }
}