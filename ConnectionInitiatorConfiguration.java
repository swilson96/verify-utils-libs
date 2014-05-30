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
    protected boolean acceptSelfSignedCerts = false;

    public boolean acceptsSelfSignedCerts() {
        return acceptSelfSignedCerts;
    }

    public JerseyClientConfiguration getClientConnectionConfig() {
        return httpClient;
    }
}
