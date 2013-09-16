package uk.gov.ida.shared.rest.truststore;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import java.security.KeyStore;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public class DependentServiceSSLTrustStore {
    private Optional<KeyStore> keyStore = absent();

    @Inject
    public DependentServiceSSLTrustStore() {
    }

    public DependentServiceSSLTrustStore(KeyStore keyStore) {
        this.keyStore = fromNullable(keyStore);
    }

    public Optional<KeyStore> getKeyStore() {
        return keyStore;
    }
}
