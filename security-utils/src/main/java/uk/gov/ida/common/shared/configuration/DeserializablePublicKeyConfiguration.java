package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.PublicKey;

@JsonDeserialize(using=PublicKeyDeserializer.class)
public class DeserializablePublicKeyConfiguration {
    private PublicKey publicKey;
    private String cert;

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private String keyUri;

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private String keyName;

    public DeserializablePublicKeyConfiguration(PublicKey publicKey, String keyUri, String keyName, String cert) {
        this.publicKey = publicKey;
        this.keyUri = keyUri;
        this.keyName = keyName;
        this.cert = cert;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getCert() {
        return cert;
    }

    public String getKeyUri() {
        return keyUri;
    }
}