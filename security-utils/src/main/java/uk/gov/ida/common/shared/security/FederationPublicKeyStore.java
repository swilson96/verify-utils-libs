package uk.gov.ida.common.shared.security;

import java.security.PublicKey;
import java.util.List;

public interface FederationPublicKeyStore {
    List<PublicKey> getVerifyingKeysForEntity(String entityId);
    PublicKey getEncryptionKeyForEntity(String entityId);
}
