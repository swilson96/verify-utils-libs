package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class BasicAuthCredentials {

    @JsonProperty
    @NotNull
    private String username;

    @JsonProperty
    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
