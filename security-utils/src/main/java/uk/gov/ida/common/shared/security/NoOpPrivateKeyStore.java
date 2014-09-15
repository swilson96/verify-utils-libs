package uk.gov.ida.common.shared.security;

import com.google.inject.Inject;

import java.security.PrivateKey;

public class NoOpPrivateKeyStore implements PrivateKeyStore {

    @Inject
    public NoOpPrivateKeyStore() {
    }

    @Override
    public PrivateKey getSigningPrivateKey() {
        throw new UnsupportedOperationException("Access to private signing key is prohibited.");
    }

    @Override
    public java.util.List<PrivateKey> getEncryptionPrivateKeys() {
        throw new UnsupportedOperationException("Access to private encryption key is prohibited.");
    }
}
