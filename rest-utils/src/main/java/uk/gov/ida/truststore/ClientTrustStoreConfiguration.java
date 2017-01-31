package uk.gov.ida.truststore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClientTrustStoreConfiguration {

    protected ClientTrustStoreConfiguration() {
    }

    @Valid
    @NotNull
    protected String storeFile;

    @Valid
    @NotNull
    @Size(min = 1)
    protected String password;

    public String getStoreFile() {
        return storeFile;
    }

    public String getPassword() {
        return password;
    }
}
