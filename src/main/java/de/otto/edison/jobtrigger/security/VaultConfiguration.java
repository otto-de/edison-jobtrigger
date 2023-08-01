package de.otto.edison.jobtrigger.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;

@Configuration
public class VaultConfiguration extends AbstractVaultConfiguration{

    private static final String INVALID_TOKEN = "00000000-0000-0000-0000-000000000000";
    private static final String LOCAL_VAULT_ADDR = "http://localhost:8200";

    @Override
    public VaultEndpoint vaultEndpoint() {
        String vaultAddr = System.getenv("VAULT_ADDR");
        if(vaultAddr == null) {
            vaultAddr = LOCAL_VAULT_ADDR;
        }
        return VaultEndpoint.from(URI.create(vaultAddr));
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        String vaultToken = System.getenv("VAULT_TOKEN");

        if (vaultToken == null)  {
            vaultToken = getVaultTokenFromFile();
        }

        if (vaultToken == null) {
            vaultToken = INVALID_TOKEN;
        }

        return new TokenAuthentication(vaultToken);
    }

    private String getVaultTokenFromFile() {
        String filename = System.getProperty("user.home") + "/.vault-token";

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename)) ) {
            return fileReader.readLine();
        } catch (Exception e) {
            return null;
        }
    }



}
