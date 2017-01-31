package uk.gov.ida.truststore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClientTrustStoreConfiguration {

    protected ClientTrustStoreConfiguration() {
    }

    @Valid
    @NotNull
    protected String path;

    @Valid
    @NotNull
    @Size(min = 1)
    protected String password;

    public String getPath() {
        return path;
    }

    public String getPassword() {
        return password;
    }
}
