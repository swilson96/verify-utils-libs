package uk.gov.ida.common.shared.security;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import javax.inject.Inject;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.configuration.PrivateEncryptionKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PrivateSigningKeyConfiguration;


import java.security.PrivateKey;
import java.util.List;

public class PrivateKeyConfigurationKeyStore implements PrivateKeyStore {

    private final KeyConfiguration signingKeyConfiguration;
    private final KeyConfiguration encryptionKeyConfiguration;
    private final LoadingCache<String, PrivateKey> keyCache;

    @Inject
    public PrivateKeyConfigurationKeyStore(
            @PrivateSigningKeyConfiguration KeyConfiguration signingKeyConfiguration,
            @PrivateEncryptionKeyConfiguration KeyConfiguration encryptionKeyConfiguration,
            LoadingCache<String, PrivateKey> keyCache) {

        this.signingKeyConfiguration = signingKeyConfiguration;
        this.encryptionKeyConfiguration = encryptionKeyConfiguration;
        this.keyCache = keyCache;
    }

    @Override
    public PrivateKey getSigningPrivateKey() {
        return keyCache.getUnchecked(signingKeyConfiguration.getKeyUri());
    }

    @Override
    public List<PrivateKey> getEncryptionPrivateKeys() {
        return ImmutableList.of(keyCache.getUnchecked(encryptionKeyConfiguration.getKeyUri()));
    }
}
