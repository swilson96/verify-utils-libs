package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BaseApplicationConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }
}
