package uk.gov.ida.common.shared.security;

import java.security.PublicKey;
import java.util.List;

public class NoNeedToAccessInternalPublicKeyStore implements InternalPublicKeyStore {
    @Override
    public List<PublicKey> getVerifyingKeysForEntity() {
        throw new UnsupportedOperationException("This service does not have access to an internal public key.");
    }

}
