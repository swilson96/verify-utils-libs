package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.File;

public class MutualAuthConfiguration {
    @JsonProperty
    @NotNull
    private File keyStoreFile;

    @JsonProperty
    @NotNull
    private String keyStorePassword;

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public File getKeyStoreFile() {
        return keyStoreFile;
    }
}
