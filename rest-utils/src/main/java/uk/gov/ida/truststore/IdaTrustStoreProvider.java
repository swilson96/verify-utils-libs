package uk.gov.ida.truststore;


import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.gov.ida.truststore.ClientTrustStoreConfiguration;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.KeyStore;

public class IdaTrustStoreProvider implements Provider<IdaTrustStore> {

    private final ClientTrustStoreConfiguration configuration;
    private final KeyStoreLoader keyStoreLoader;

    @Inject
    public IdaTrustStoreProvider(ClientTrustStoreConfiguration configuration, KeyStoreLoader keyStoreLoader) {
        this.configuration = configuration;
        this.keyStoreLoader = keyStoreLoader;
    }

    @Override
    public IdaTrustStore get() {
        KeyStore ks = keyStoreLoader.load(configuration.getStoreUri(), configuration.getPassword());
        return new IdaTrustStore(ks);
    }
}
