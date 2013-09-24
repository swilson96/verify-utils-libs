package uk.gov.ida.shared.rest.truststore;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import java.security.KeyStore;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public class IdaTrustStore {
    private Optional<KeyStore> keyStore = absent();

    @Inject
    public IdaTrustStore() {
        //NOTE: [24/09/2013] Mark - This constructor is to allow injection in places that don't want to care about the trust-store
    }

    public IdaTrustStore(KeyStore keyStore) {
        this.keyStore = fromNullable(keyStore);
    }

    public Optional<KeyStore> getKeyStore() {
        return keyStore;
    }
}
