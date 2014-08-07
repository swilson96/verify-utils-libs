package uk.gov.ida.configuration.utils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClientTrustStoreConfiguration {

    protected ClientTrustStoreConfiguration() {
    }

    @Valid
    @NotNull
    protected String storeUri;

    @Valid
    @NotNull
    @Size(min = 1)
    protected String password;

    public String getStoreUri() {
        return storeUri;
    }

    public String getPassword() {
        return password;
    }
}
