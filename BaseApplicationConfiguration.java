package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BaseApplicationConfiguration extends Configuration implements ServiceNameConfiguration {

    @Valid
    @NotNull
    @JsonProperty
    public String serviceName;

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public String getShortServiceName() {
        return serviceName.replaceFirst("-agent$", "");
    }
}
