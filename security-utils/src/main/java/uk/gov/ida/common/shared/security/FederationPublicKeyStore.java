package uk.gov.ida.common.shared.security;

import uk.gov.ida.truststore.IdaTrustStore;

import java.security.PublicKey;
import java.util.List;

public interface FederationPublicKeyStore {
    List<PublicKey> getVerifyingKeysForEntity(String entityId, IdaTrustStore trustStore);
    PublicKey getEncryptionKeyForEntity(String entityId, IdaTrustStore trustStore);
}
