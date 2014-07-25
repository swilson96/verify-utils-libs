package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class ConnectionInitiatorConfiguration extends BaseApplicationConfiguration {

    @Valid
    @JsonProperty
    @NotNull
    @SuppressWarnings("unused")
    private JerseyClientConfiguration httpClient;

    @JsonProperty
    protected boolean acceptAllSslCerts = false;

    @JsonProperty
    public MutualAuthConfiguration mutualAuth = null;

    @JsonProperty
    @Valid
    public TrustedServerConfiguration trustedServers = null;

    public MutualAuthConfiguration getMutualAuth() {
        return mutualAuth;
    }

    public boolean acceptAllSslCerts() {
        return acceptAllSslCerts;
    }

    public JerseyClientConfiguration getClientConnectionConfig() {
        return httpClient;
    }

    public TrustedServerConfiguration getTrustedServers() {
        return trustedServers;
    }
}
