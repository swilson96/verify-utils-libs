package uk.gov.ida.common.shared.security;

import java.security.PrivateKey;

public interface PrivateKeyStore {
    PrivateKey getSigningPrivateKey();
    PrivateKey getEncryptionPrivateKey();
}
