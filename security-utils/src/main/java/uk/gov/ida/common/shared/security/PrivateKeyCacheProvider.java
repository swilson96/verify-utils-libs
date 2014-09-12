package uk.gov.ida.common.shared.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.configuration.PrivateEncryptionKeyConfiguration;
import uk.gov.ida.common.shared.configuration.PrivateSigningKeyConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

public class PrivateKeyCacheProvider implements Provider<LoadingCache<String, PrivateKey>> {
    private static final String ENCRYPTION_KEY_ENVIRONMENT_VARIABLE = "ENCRYPTION_KEY";
    private static final String SIGNING_KEY_ENVIRONMENT_VARIABLE = "SIGNING_KEY";
    private KeyConfiguration signingKeyConfiguration;
    private KeyConfiguration encryptionKeyConfiguration;
    private PrivateKeyFactory privateKeyFactory;
    private LoadingCache<String, PrivateKey> initializedValue;

    @Inject
    public PrivateKeyCacheProvider(
            @PrivateSigningKeyConfiguration KeyConfiguration signingKeyConfiguration,
            @PrivateEncryptionKeyConfiguration KeyConfiguration encryptionKeyConfiguration,
            PrivateKeyFactory privateKeyFactory) {

        this.signingKeyConfiguration = signingKeyConfiguration;
        this.encryptionKeyConfiguration = encryptionKeyConfiguration;
        this.privateKeyFactory = privateKeyFactory;
        initializedValue = init();
    }

    @Override
    public LoadingCache<String, PrivateKey> get() {
        return initializedValue;
    }

    private LoadingCache<String, PrivateKey> init() {
        LoadingCache<String, PrivateKey> keyCache = CacheBuilder.<String, PrivateKey>newBuilder()
                .build(new CacheLoader<String, PrivateKey>() {
                    @Override
                    public PrivateKey load(String key) throws IOException {
                        return getPrivateKey(key);
                    }
                });

        try {
            keyCache.get(getSigningKeyPath());
            keyCache.get(getEncryptionKeyPath());
        } catch (ExecutionException e) {
           // Swallowing this exception because this is simply warming up the cache
        }

        return keyCache;
    }

    private String getSigningKeyPath() {
        String signingKeyPathFromEnv = System.getenv(SIGNING_KEY_ENVIRONMENT_VARIABLE);
        return signingKeyPathFromEnv == null ? signingKeyConfiguration.getKeyUri() : signingKeyPathFromEnv;
    }

    private String getEncryptionKeyPath() {
        String encryptionKeyPathFromEnv = System.getenv(ENCRYPTION_KEY_ENVIRONMENT_VARIABLE);
        return encryptionKeyPathFromEnv == null ? encryptionKeyConfiguration.getKeyUri() : encryptionKeyPathFromEnv;
    }

    private PrivateKey getPrivateKey(String keyUri) throws IOException {
        return privateKeyFactory.createPrivateKey(Files.toByteArray(new File(keyUri)));
    }
}
