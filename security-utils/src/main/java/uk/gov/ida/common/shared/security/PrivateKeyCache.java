package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.security.exceptions.KeyLoadingException;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;

public class PrivateKeyCache implements PrivateKeyStore {
    private static final String PRIMARY_ENCRYPTION_KEY_ENVIRONMENT_VARIABLE = "PRIMARY_ENCRYPTION_KEY";
    private static final String SECONDARY_ENCRYPTION_KEY_ENVIRONMENT_VARIABLE = "SECONDARY_ENCRYPTION_KEY";
    private static final String SIGNING_KEY_ENVIRONMENT_VARIABLE = "SIGNING_KEY";
    private KeyConfiguration signingKeyConfiguration;
    private KeyConfiguration primaryEncryptionKeyConfiguration;
    private KeyConfiguration secondaryEncryptionKeyConfiguration;
    private PrivateKeyFactory privateKeyFactory;

    private PrivateKey signingKey;
    private PrivateKey primaryEncryptionKey;
    private PrivateKey secondaryEncryptionKey;

    public PrivateKeyCache(
            KeyConfiguration signingKeyConfiguration,
            KeyConfiguration primaryEncryptionKeyConfiguration,
            KeyConfiguration secondaryEncryptionKeyConfiguration,
            PrivateKeyFactory privateKeyFactory) {

        this.signingKeyConfiguration = signingKeyConfiguration;
        this.primaryEncryptionKeyConfiguration = primaryEncryptionKeyConfiguration;
        this.secondaryEncryptionKeyConfiguration = secondaryEncryptionKeyConfiguration;
        this.privateKeyFactory = privateKeyFactory;
        this.signingKey = getSigningKey();
        this.primaryEncryptionKey = getPrimaryEncryptionKey();
        this.secondaryEncryptionKey = getSecondaryEncryptionKey();
    }

    private PrivateKey getSigningKey() {
        String signingKeyPathFromEnv = System.getenv(SIGNING_KEY_ENVIRONMENT_VARIABLE);
        String signingKeyPath = signingKeyPathFromEnv == null ? signingKeyConfiguration.getKeyUri() : signingKeyPathFromEnv;
        return getPrivateKey(signingKeyPath);
    }

    private PrivateKey getPrimaryEncryptionKey() {
        String encryptionKeyPathFromEnv = System.getenv(PRIMARY_ENCRYPTION_KEY_ENVIRONMENT_VARIABLE);
        String encryptionKeyPath = encryptionKeyPathFromEnv == null ? primaryEncryptionKeyConfiguration.getKeyUri() : encryptionKeyPathFromEnv;
        return getPrivateKey(encryptionKeyPath);
    }

    private PrivateKey getSecondaryEncryptionKey() {
        String encryptionKeyPathFromEnv = System.getenv(SECONDARY_ENCRYPTION_KEY_ENVIRONMENT_VARIABLE);
        String encryptionKeyPath = encryptionKeyPathFromEnv == null ? secondaryEncryptionKeyConfiguration.getKeyUri() : encryptionKeyPathFromEnv;
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
    public java.util.List<PrivateKey> getEncryptionPrivateKeys() {
        return ImmutableList.of(primaryEncryptionKey, secondaryEncryptionKey);
    }
}
