package uk.gov.ida.common.shared.security;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.security.exceptions.KeyLoadingException;

import java.io.File;
import java.security.PrivateKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class, PrivateKeyCache.class})
public class PrivateKeyCacheTest {
    @Mock
    private KeyConfiguration signingKeyConfiguration;
    @Mock
    private KeyConfiguration primaryEncryptionKeyConfiguration;
    @Mock
    private KeyConfiguration secondaryEncryptionKeyConfiguration;
    @Mock
    private PrivateKeyFactory privateKeyFactory;
    @Mock
    private PrivateKey signingPrivateKey;
    @Mock
    private PrivateKey primaryEncryptionPrivateKey;
    @Mock
    private PrivateKey secondaryEncryptionPrivateKey;

    private final String nonExistentFilePath = "/boo/encryption-key-uri";
    private final String tempSigningKeyUri = "/tmp/signing-key-uri";
    private final String signingKeyUri = "/signing-key-uri";
    private final String primaryEncryptionKeyUri = "/primary-encryption-key-uri";
    private final String secondaryEncryptionKeyUri = "/secondary-encryption-key-uri";
    private final byte[] signingKey = new byte[] {1,2,3};
    private final byte[] primaryEncryptionKey = new byte[] {4,5,6};
    private final byte[] secondaryEncryptionKey = new byte[] {7,8,9};
    private PrivateKeyCache privateKeyCache;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("ENCRYPTION_KEY")).thenReturn(null);
        when(System.getenv("SIGNING_KEY")).thenReturn(null);

        when(signingKeyConfiguration.getKeyUri()).thenReturn(signingKeyUri);
        when(primaryEncryptionKeyConfiguration.getKeyUri()).thenReturn(primaryEncryptionKeyUri);
        when(secondaryEncryptionKeyConfiguration.getKeyUri()).thenReturn(secondaryEncryptionKeyUri);
        when(privateKeyFactory.createPrivateKey(eq(signingKey))).thenReturn(signingPrivateKey);
        when(privateKeyFactory.createPrivateKey(eq(primaryEncryptionKey))).thenReturn(primaryEncryptionPrivateKey);
        when(privateKeyFactory.createPrivateKey(eq(secondaryEncryptionKey))).thenReturn(secondaryEncryptionPrivateKey);
    }

    @Test
    public void init_shouldLoadSigningKeyFromDisk() throws Exception {
        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(primaryEncryptionKeyUri))).thenReturn(primaryEncryptionKey);


        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                primaryEncryptionKeyConfiguration,
                secondaryEncryptionKeyConfiguration,
                privateKeyFactory
        );

        PrivateKey privateKey = privateKeyCache.getSigningPrivateKey();
        assertThat(privateKey).isEqualTo(signingPrivateKey);
        verifyStatic(times(1));
    }

    @Test
    public void init_shouldLoadEncryptionKeyFromDisk() throws Exception {
        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(primaryEncryptionKeyUri))).thenReturn(primaryEncryptionKey);
        when(Files.toByteArray(new File(secondaryEncryptionKeyUri))).thenReturn(secondaryEncryptionKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                primaryEncryptionKeyConfiguration,
                secondaryEncryptionKeyConfiguration,
                privateKeyFactory
        );

        List<PrivateKey> encryptionPrivateKeys = privateKeyCache.getEncryptionPrivateKeys();
        assertThat(encryptionPrivateKeys).hasSize(2);
        assertThat(encryptionPrivateKeys.get(0)).isEqualTo(primaryEncryptionPrivateKey);
        assertThat(encryptionPrivateKeys.get(1)).isEqualTo(secondaryEncryptionPrivateKey);
        verifyStatic(times(1));
    }

    @Test
    public void init_shouldLoadSigningKeyFromLocationAtEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("SIGNING_KEY")).thenReturn(tempSigningKeyUri);

        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(primaryEncryptionKeyUri))).thenReturn(primaryEncryptionKey);
        when(Files.toByteArray(new File(tempSigningKeyUri))).thenReturn(signingKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                primaryEncryptionKeyConfiguration,
                primaryEncryptionKeyConfiguration,
                privateKeyFactory
        );

        PrivateKey privateKey = privateKeyCache.getSigningPrivateKey();
        assertThat(privateKey).isEqualTo(signingPrivateKey);
    }

    @Test
    public void init_shouldLoadEncryptionKeyFromLocationAtEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("PRIMARY_ENCRYPTION_KEY")).thenReturn(primaryEncryptionKeyUri);
        when(System.getenv("SECONDARY_ENCRYPTION_KEY")).thenReturn(secondaryEncryptionKeyUri);

        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(primaryEncryptionKeyUri))).thenReturn(primaryEncryptionKey);
        when(Files.toByteArray(new File(secondaryEncryptionKeyUri))).thenReturn(secondaryEncryptionKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                null,
                null,
                privateKeyFactory
        );

        List<PrivateKey> encryptionPrivateKeys = privateKeyCache.getEncryptionPrivateKeys();
        assertThat(encryptionPrivateKeys).hasSize(2);
        assertThat(encryptionPrivateKeys.get(0)).isEqualTo(primaryEncryptionPrivateKey);
        assertThat(encryptionPrivateKeys.get(1)).isEqualTo(secondaryEncryptionPrivateKey);
    }
    
    @Test(expected = KeyLoadingException.class)
    public void init_shouldThrowExceptionIfKeyFileNotFoundAtPathFromEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("SIGNING_KEY")).thenReturn(nonExistentFilePath);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                primaryEncryptionKeyConfiguration,
                secondaryEncryptionKeyConfiguration,
                privateKeyFactory
        );

        privateKeyCache.getSigningPrivateKey();
    }

}
