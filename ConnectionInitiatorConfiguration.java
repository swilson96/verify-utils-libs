package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;

public abstract class ConnectionInitiatorConfiguration extends BaseApplicationConfiguration {

    @Valid
    @JsonProperty
    @NotNull
    @SuppressWarnings("unused")
    private JerseyClientConfiguration httpClient;

    @JsonProperty
    protected boolean acceptSelfSignedCerts = false;

    public MutualAuthConfiguration getMutualAuth() {
        return mutualAuth;
    }

    @JsonProperty
    protected MutualAuthConfiguration mutualAuth = null;

    public boolean acceptsSelfSignedCerts() {
        return acceptSelfSignedCerts;
    }

    public JerseyClientConfiguration getClientConnectionConfig() {
        return httpClient;
    }

    public static class MutualAuthConfiguration {
        @JsonProperty
        @NotNull
        private File keyStoreFile;

        @JsonProperty
        @NotNull
        private String keyStorePassword;

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public File getKeyStoreFile() {
            return keyStoreFile;
        }
    }
}
