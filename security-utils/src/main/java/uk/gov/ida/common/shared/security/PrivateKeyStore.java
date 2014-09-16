package uk.gov.ida.common.shared.security;

import java.security.PrivateKey;

public interface PrivateKeyStore {
    PrivateKey getSigningPrivateKey();
    java.util.List<PrivateKey> getEncryptionPrivateKeys();
}
