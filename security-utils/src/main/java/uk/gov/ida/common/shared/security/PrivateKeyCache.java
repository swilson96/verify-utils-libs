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
import uk.gov.ida.common.shared.security.exceptions.KeyLoadingException;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;

public class PrivateKeyCache implements PrivateKeyStore {
    private static final String ENCRYPTION_KEY_ENVIRONMENT_VARIABLE = "ENCRYPTION_KEY";
    private static final String SIGNING_KEY_ENVIRONMENT_VARIABLE = "SIGNING_KEY";
    private KeyConfiguration signingKeyConfiguration;
    private KeyConfiguration encryptionKeyConfiguration;
    private PrivateKeyFactory privateKeyFactory;

    private PrivateKey signingKey;
    private PrivateKey encryptionKey;

    @Inject
    public PrivateKeyCache(
            @PrivateSigningKeyConfiguration KeyConfiguration signingKeyConfiguration,
            @PrivateEncryptionKeyConfiguration KeyConfiguration encryptionKeyConfiguration,
            PrivateKeyFactory privateKeyFactory) {

        this.signingKeyConfiguration = signingKeyConfiguration;
        this.encryptionKeyConfiguration = encryptionKeyConfiguration;
        this.privateKeyFactory = privateKeyFactory;
        this.signingKey = getSigningKey();
        this.encryptionKey = getEncryptionKey();
    }

    private PrivateKey getSigningKey() {
        String signingKeyPathFromEnv = System.getenv(SIGNING_KEY_ENVIRONMENT_VARIABLE);
        String signingKeyPath = signingKeyPathFromEnv == null ? signingKeyConfiguration.getKeyUri() : signingKeyPathFromEnv;
        return getPrivateKey(signingKeyPath);
    }

    private PrivateKey getEncryptionKey() {
        String encryptionKeyPathFromEnv = System.getenv(ENCRYPTION_KEY_ENVIRONMENT_VARIABLE);
        String encryptionKeyPath = encryptionKeyPathFromEnv == null ? encryptionKeyConfiguration.getKeyUri() : encryptionKeyPathFromEnv;
        return getPrivateKey(encryptionKeyPath);
    }

    private PrivateKey getPrivateKey(String keyUri) {
        try {
            return privateKeyFactory.createPrivateKey(Files.toByteArray(new File(keyUri)));
        } catch (IOException e) {
            throw new KeyLoadingException(keyUri, e);
        }
    }

    @Override
    public PrivateKey getSigningPrivateKey() {
        return signingKey;
    }

    @Override
    public PrivateKey getEncryptionPrivateKey() {
        return encryptionKey;
    }
}
