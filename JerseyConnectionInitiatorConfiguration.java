package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class JerseyConnectionInitiatorConfiguration extends BaseApplicationConfiguration implements ConnectionInitiatorConfiguration {

    @Valid
    @JsonProperty
    @NotNull
    @SuppressWarnings("unused")
    private JerseyClientConfiguration httpClient;

    @JsonProperty
    public MutualAuthConfiguration mutualAuth = null;

    @JsonProperty
    @Valid
    public TrustedServerConfiguration trustedServers = null;

    @JsonProperty
    public boolean acceptAllSslCerts = false;

    public JerseyClientConfiguration getClientConnectionConfig() {
        return httpClient;
    }

    @Override
    public TrustedServerConfiguration getTrustedServers() {
        return trustedServers;
    }

    @Override
    public MutualAuthConfiguration getMutualAuth() {
        return mutualAuth;
    }

    @Override
    public boolean acceptAllSslCerts() {
        return acceptAllSslCerts;
    }
}
