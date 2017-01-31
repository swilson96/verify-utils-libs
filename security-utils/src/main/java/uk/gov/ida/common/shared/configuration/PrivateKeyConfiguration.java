package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.PrivateKey;

@SuppressWarnings("unused")
@JsonDeserialize(using=PrivateKeyDeserializer.class)
public class PrivateKeyConfiguration {

    public PrivateKeyConfiguration(PrivateKey privateKey, String keyFile) {
        this.privateKey = privateKey;
        this.keyFile = keyFile;
    }

    private PrivateKey privateKey;

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private String keyFile;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
