package uk.gov.ida.truststore;


import com.google.inject.Inject;
import com.google.inject.Provider;

import java.security.KeyStore;

public class KeyStoreProvider implements Provider<KeyStore> {

    private final ClientTrustStoreConfiguration configuration;
    private final KeyStoreLoader keyStoreLoader;

    @Inject
    public KeyStoreProvider(ClientTrustStoreConfiguration configuration, KeyStoreLoader keyStoreLoader) {
        this.configuration = configuration;
        this.keyStoreLoader = keyStoreLoader;
    }

    @Override
    public KeyStore get() {
        KeyStore ks = keyStoreLoader.load(configuration.getStoreUri(), configuration.getPassword());
        return ks;
    }
}
