package uk.gov.ida.common.shared.security;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    private final byte[] signingKey = new byte[] {1,2,3,5};
    private final File signingKeyFile = createTempFile("signing-key", new String(signingKey));
    private final String signingKeyUri = signingKeyFile.getAbsolutePath();
    private final byte[] encryptionKey = new byte[] {1,2,3,4};
    private final File encryptionKeyFile = createTempFile("encryption-key", new String(encryptionKey));
    private final String encryptionKeyUri = encryptionKeyFile.getAbsolutePath();

    private PrivateKeyCacheProvider provider;

    @Before
    public void setUp() throws Exception {
        when(signingKeyConfiguration.getKeyUri()).thenReturn(signingKeyUri);
        when(encryptionKeyConfiguration.getKeyUri()).thenReturn(encryptionKeyUri);
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
    }

    @Test
    public void get_shouldLoadEncryptionKeyFromDisk() throws Exception {
        final Cache<String, PrivateKey> keyCache = provider.get();

        assertThat(keyCache.asMap().containsKey(encryptionKeyUri)).isEqualTo(true);
        assertThat(keyCache.asMap().get(encryptionKeyUri)).isEqualTo(encryptionPrivateKey);
    }

    private File createTempFile(String filename, String content) {
        try{
            File temp = File.createTempFile(filename, ".tmp");

            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            bw.write(content);
            bw.close();

            return temp;
        }catch(IOException e){
            throw Throwables.propagate(e);
        }
    }
}
