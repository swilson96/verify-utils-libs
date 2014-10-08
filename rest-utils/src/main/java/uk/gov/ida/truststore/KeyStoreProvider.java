package uk.gov.ida.truststore;


import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.security.KeyStore;

public class KeyStoreProvider implements Provider<Optional<KeyStore>> {

    private final ClientTrustStoreConfiguration configuration;
    private final KeyStoreLoader keyStoreLoader;

    @Inject
    public KeyStoreProvider(ClientTrustStoreConfiguration configuration, KeyStoreLoader keyStoreLoader) {
        this.configuration = configuration;
        this.keyStoreLoader = keyStoreLoader;
    }

    @Override
    public Optional<KeyStore> get() {
        KeyStore ks = keyStoreLoader.load(configuration.getStoreUri(), configuration.getPassword());
        return Optional.fromNullable(ks);
    }
}
