package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;

import java.security.PrivateKey;
import java.util.List;

public class PrivateKeyCache implements PrivateKeyStore {
    private NumberedPipeReader numberedPipeReader;

    private PrivateKey signingKey;
    private PrivateKey primaryEncryptionKey;
    private PrivateKey secondaryEncryptionKey;

    private static final int PRIMARY_ENCRYPTION_PRIVATE_KEY_FD = 4;
    private static final int SECONDARY_ENCRYPTION_PRIVATE_KEY_FD = 5;
    private static final int SIGNING_PRIVATE_KEY_FD = 6;

    public PrivateKeyCache(NumberedPipeReader numberedPipeReader) {
        this.numberedPipeReader = numberedPipeReader;
        getKeys();
    }

    @Deprecated
    public PrivateKeyCache(
            KeyConfiguration signingKeyConfiguration,
            KeyConfiguration primaryEncryptionKeyConfiguration,
            KeyConfiguration secondaryEncryptionKeyConfiguration,
            PrivateKeyFactory privateKeyFactory,
            NumberedPipeReader numberedPipeReader,
            boolean secureKeyManagementEnabled) {
        this(numberedPipeReader);
    }

    @Override
    public PrivateKey getSigningPrivateKey() {
        return signingKey;
    }

    @Override
    public List<PrivateKey> getEncryptionPrivateKeys() {
        return ImmutableList.of(primaryEncryptionKey, secondaryEncryptionKey);
    }

    private void getKeys() {
        this.signingKey = this.numberedPipeReader.readKey(SIGNING_PRIVATE_KEY_FD);
        this.primaryEncryptionKey = this.numberedPipeReader.readKey(PRIMARY_ENCRYPTION_PRIVATE_KEY_FD);
        this.secondaryEncryptionKey = this.numberedPipeReader.readKey(SECONDARY_ENCRYPTION_PRIVATE_KEY_FD);
    }
}
