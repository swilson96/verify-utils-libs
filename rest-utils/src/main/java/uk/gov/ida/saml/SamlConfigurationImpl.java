package uk.gov.ida.saml;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.saml.configuration.SamlConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class SamlConfigurationImpl implements SamlConfiguration {
    protected SamlConfigurationImpl() {
    }

    @Valid
    @NotNull
    @JsonProperty
    protected String entityId;

    @Valid
    @JsonProperty
    protected URI expectedDestination = URI.create("http://configure.me/if/i/fail");

    public String getEntityId() {
        return entityId;
    }

    public URI getExpectedDestinationHost() {
        return expectedDestination;

    }

}
