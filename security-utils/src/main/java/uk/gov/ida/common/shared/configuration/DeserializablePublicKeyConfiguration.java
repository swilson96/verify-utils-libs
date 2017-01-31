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
    private String certFile;

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    private String name;

    public DeserializablePublicKeyConfiguration(PublicKey publicKey, String certFile, String name, String cert) {
        this.publicKey = publicKey;
        this.certFile = certFile;
        this.name = name;
        this.cert = cert;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getName() {
        return name;
    }

    public String getCert() {
        return cert;
    }

    public String getCertFile() {
        return certFile;
    }
}