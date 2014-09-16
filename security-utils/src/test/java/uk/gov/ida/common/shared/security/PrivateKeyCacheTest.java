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
    private KeyConfiguration encryptionKeyConfiguration;
    @Mock
    private PrivateKeyFactory privateKeyFactory;
    @Mock
    private PrivateKey signingPrivateKey;
    @Mock
    private PrivateKey encryptionPrivateKey;

    private final String tempEncrpytionKeyUri = "/tmp/encryption-key-uri";
    private final String nonExistentFilePath = "/boo/encryption-key-uri";
    private final String tempSigningKeyUri = "/tmp/signing-key-uri";
    private final String signingKeyUri = "/signing-key-uri";
    private final String encryptionKeyUri = "/encryption-key-uri";
    private final byte[] signingKey = new byte[] {1,2,3};
    private final byte[] encryptionKey = new byte[] {4,5,6};

    private PrivateKeyCache privateKeyCache;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("ENCRYPTION_KEY")).thenReturn(null);
        when(System.getenv("SIGNING_KEY")).thenReturn(null);

        when(signingKeyConfiguration.getKeyUri()).thenReturn(signingKeyUri);
        when(encryptionKeyConfiguration.getKeyUri()).thenReturn(encryptionKeyUri);
        when(privateKeyFactory.createPrivateKey(eq(signingKey))).thenReturn(signingPrivateKey);
        when(privateKeyFactory.createPrivateKey(eq(encryptionKey))).thenReturn(encryptionPrivateKey);
    }

    @Test
    public void init_shouldLoadSigningKeyFromDisk() throws Exception {
        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(encryptionKeyUri))).thenReturn(encryptionKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
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
        when(Files.toByteArray(new File(encryptionKeyUri))).thenReturn(encryptionKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
                privateKeyFactory
        );

        PrivateKey privateKey = privateKeyCache.getEncryptionPrivateKeys().get(0);
        assertThat(privateKey).isEqualTo(encryptionPrivateKey);
        verifyStatic(times(1));
    }

    @Test
    public void init_shouldLoadSigningKeyFromLocationAtEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("SIGNING_KEY")).thenReturn(tempSigningKeyUri);

        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(encryptionKeyUri))).thenReturn(encryptionKey);
        when(Files.toByteArray(new File(tempSigningKeyUri))).thenReturn(signingKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
                privateKeyFactory
        );

        PrivateKey privateKey = privateKeyCache.getSigningPrivateKey();
        assertThat(privateKey).isEqualTo(signingPrivateKey);
    }

    @Test
    public void init_shouldLoadEncryptionKeyFromLocationAtEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("ENCRYPTION_KEY")).thenReturn(tempEncrpytionKeyUri);

        PowerMockito.mockStatic(Files.class);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(tempEncrpytionKeyUri))).thenReturn(encryptionKey);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
                privateKeyFactory
        );

        PrivateKey privateKey = privateKeyCache.getEncryptionPrivateKeys().get(0);
        assertThat(privateKey).isEqualTo(encryptionPrivateKey);
    }
    
    @Test(expected = KeyLoadingException.class)
    public void init_shouldThrowExceptionIfKeyFileNotFoundAtPathFromEnvironmentVariable() throws Exception {
        PowerMockito.mockStatic(System.class);
        when(System.getenv("SIGNING_KEY")).thenReturn(nonExistentFilePath);

        privateKeyCache = new PrivateKeyCache(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
                privateKeyFactory
        );

        privateKeyCache.getSigningPrivateKey();
    }

}
