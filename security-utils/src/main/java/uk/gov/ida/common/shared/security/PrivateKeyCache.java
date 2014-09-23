package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.security.exceptions.KeyLoadingException;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;

public class PrivateKeyCache implements PrivateKeyStore {
    private KeyConfiguration signingKeyConfiguration;
    private KeyConfiguration primaryEncryptionKeyConfiguration;
    private KeyConfiguration secondaryEncryptionKeyConfiguration;
    private PrivateKeyFactory privateKeyFactory;
    private NumberedPipeReader numberedPipeReader;
    private boolean secureKeyManagementEnabled;
    private PrivateKey signingKey;

    private PrivateKey primaryEncryptionKey;
    private PrivateKey secondaryEncryptionKey;

    private static final int PRIMARY_ENCRYPTION_PRIVATE_KEY_FD = 4;
    private static final int SECONDARY_ENCRYPTION_PRIVATE_KEY_FD = 5;
    private static final int SIGNING_PRIVATE_KEY_FD = 6;

    public PrivateKeyCache(
            KeyConfiguration signingKeyConfiguration,
            KeyConfiguration primaryEncryptionKeyConfiguration,
            KeyConfiguration secondaryEncryptionKeyConfiguration,
            PrivateKeyFactory privateKeyFactory,
            NumberedPipeReader numberedPipeReader,
            boolean secureKeyManagementEnabled) {

        this.secureKeyManagementEnabled = secureKeyManagementEnabled;
        this.signingKeyConfiguration = signingKeyConfiguration;
        this.primaryEncryptionKeyConfiguration = primaryEncryptionKeyConfiguration;
        this.secondaryEncryptionKeyConfiguration = secondaryEncryptionKeyConfiguration;
        this.privateKeyFactory = privateKeyFactory;
        this.numberedPipeReader = numberedPipeReader;
        getKeys();
    }

    @Override
    public PrivateKey getSigningPrivateKey() {
        return signingKey;
    }

    @Override
    public java.util.List<PrivateKey> getEncryptionPrivateKeys() {
        return ImmutableList.of(primaryEncryptionKey, secondaryEncryptionKey);
    }

    private void getKeys() {
        if(this.secureKeyManagementEnabled){
            this.signingKey = this.numberedPipeReader.readKey(SIGNING_PRIVATE_KEY_FD);
            this.primaryEncryptionKey = this.numberedPipeReader.readKey(PRIMARY_ENCRYPTION_PRIVATE_KEY_FD);
            this.secondaryEncryptionKey = this.numberedPipeReader.readKey(SECONDARY_ENCRYPTION_PRIVATE_KEY_FD);
        }
        else {
            this.signingKey = getSigningKeyFromConfigUri();
            this.primaryEncryptionKey = getPrimaryEncryptionKeyFromConfigUri();
            this.secondaryEncryptionKey = getSecondaryEncryptionKeyFromUri();
        }
    }

    private PrivateKey getSigningKeyFromConfigUri() {
            String signingKeyPath = signingKeyConfiguration.getKeyUri();
            return getPrivateKey(signingKeyPath);
    }

    private PrivateKey getPrimaryEncryptionKeyFromConfigUri() {
        String encryptionKeyPath = primaryEncryptionKeyConfiguration.getKeyUri();
        return getPrivateKey(encryptionKeyPath);
    }

    private PrivateKey getSecondaryEncryptionKeyFromUri() {
            String encryptionKeyPath = secondaryEncryptionKeyConfiguration.getKeyUri();
            return getPrivateKey(encryptionKeyPath);
    }

    private PrivateKey getPrivateKey(String keyUri) {
        try {
            return privateKeyFactory.createPrivateKey(Files.toByteArray(new File(keyUri)));
        } catch (IOException e) {
            throw new KeyLoadingException(keyUri, e);
        }
    }
}
