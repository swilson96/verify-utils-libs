package uk.gov.ida.common.shared.security;

import com.google.common.cache.Cache;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;

import java.io.File;
import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Files.class})
public class PrivateKeyCacheProviderTest {
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

    private final String signingKeyUri = "/signing-key-uri";
    private final String encryptionKeyUri = "/encryption-key-uri";
    private final byte[] signingKey = new byte[] {1,2,3};
    private final byte[] encryptionKey = new byte[] {4,5,6};

    private PrivateKeyCacheProvider provider;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Files.class);
        when(signingKeyConfiguration.getKeyUri()).thenReturn(signingKeyUri);
        when(encryptionKeyConfiguration.getKeyUri()).thenReturn(encryptionKeyUri);
        when(Files.toByteArray(new File(signingKeyUri))).thenReturn(signingKey);
        when(Files.toByteArray(new File(encryptionKeyUri))).thenReturn(encryptionKey);
        when(privateKeyFactory.createPrivateKey(eq(signingKey))).thenReturn(signingPrivateKey);
        when(privateKeyFactory.createPrivateKey(eq(encryptionKey))).thenReturn(encryptionPrivateKey);

        provider = new PrivateKeyCacheProvider(
                signingKeyConfiguration,
                encryptionKeyConfiguration,
                privateKeyFactory
        );
    }

    @Test
    public void get_shouldLoadSigningKeyFromDisk() throws Exception {
        final Cache<String, PrivateKey> keyCache = provider.get();

        assertThat(keyCache.asMap().containsKey(signingKeyUri)).isEqualTo(true);
        assertThat(keyCache.asMap().get(signingKeyUri)).isEqualTo(signingPrivateKey);
        verifyStatic(times(1));
        Files.toByteArray(new File(signingKeyUri));
    }

    @Test
    public void get_shouldLoadEncryptionKeyFromDisk() throws Exception {
        final Cache<String, PrivateKey> keyCache = provider.get();

        assertThat(keyCache.asMap().containsKey(encryptionKeyUri)).isEqualTo(true);
        assertThat(keyCache.asMap().get(encryptionKeyUri)).isEqualTo(encryptionPrivateKey);
        verifyStatic(times(1));
        Files.toByteArray(new File(encryptionKeyUri));
    }
}
