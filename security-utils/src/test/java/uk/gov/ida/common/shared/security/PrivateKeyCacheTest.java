package uk.gov.ida.common.shared.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.PrivateKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrivateKeyCacheTest {
    @Mock
    private PrivateKey signingPrivateKey;
    @Mock
    private PrivateKey primaryEncryptionPrivateKey;
    @Mock
    private PrivateKey secondaryEncryptionPrivateKey;
    @Mock
    private NumberedPipeReader numberedPipeReader;

    @Test
    public void init_shouldLoadKeysFromFileDescriptor() throws Exception {
        when(numberedPipeReader.readKey(4)).thenReturn(primaryEncryptionPrivateKey);
        when(numberedPipeReader.readKey(5)).thenReturn(secondaryEncryptionPrivateKey);
        when(numberedPipeReader.readKey(6)).thenReturn(signingPrivateKey);

        PrivateKeyCache privateKeyCache = new PrivateKeyCache(numberedPipeReader);

        List<PrivateKey> encryptionPrivateKeys = privateKeyCache.getEncryptionPrivateKeys();
        PrivateKey actualSigningPrivateKey = privateKeyCache.getSigningPrivateKey();
        assertThat(encryptionPrivateKeys.get(0)).isEqualTo(primaryEncryptionPrivateKey);
        assertThat(encryptionPrivateKeys.get(1)).isEqualTo(secondaryEncryptionPrivateKey);
        assertThat(actualSigningPrivateKey).isEqualTo(signingPrivateKey);
    }
}
