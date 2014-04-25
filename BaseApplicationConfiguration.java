package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class BaseApplicationConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private String serviceName;

    @Valid
    @NotNull
    @JsonProperty
    private URI serviceBaseUri;

    public String getServiceName() {
        return serviceName;
    }

    public URI getServiceBaseUri() {
        return serviceBaseUri;
    }
}
