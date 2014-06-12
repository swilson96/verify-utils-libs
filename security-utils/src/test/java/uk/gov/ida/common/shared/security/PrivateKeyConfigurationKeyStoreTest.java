package uk.gov.ida.common.shared.security;

import com.google.common.cache.LoadingCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.security.PrivateKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.ida.common.shared.security.builders.KeyConfigurationBuilder.aKeyConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class PrivateKeyConfigurationKeyStoreTest {

    private PrivateKeyConfigurationKeyStore keyStore;

    private final String signingKeyUri = URI.create("/signing-uri").toASCIIString();
    private final String encryptionKeyUri = URI.create("/encryption-uri").toASCIIString();
    @Mock
    private LoadingCache<String, PrivateKey> keyCache;
    @Mock
    private PrivateKey signingKey;
    @Mock
    private PrivateKey encryptionKey;

    @Before
    public void setUp() throws Exception {
        keyStore = new PrivateKeyConfigurationKeyStore(
                aKeyConfiguration().withKeyUri(signingKeyUri).build(),
                aKeyConfiguration().withKeyUri(encryptionKeyUri).build(),
                keyCache);
    }

    @Test
    public void getSigningPrivateKey_returnsSigningKey() throws Exception {
        when(keyCache.getUnchecked(signingKeyUri)).thenReturn(signingKey);

        PrivateKey retrievedKey = keyStore.getSigningPrivateKey();

        assertThat(retrievedKey).isEqualTo(signingKey);
    }

    @Test
    public void getEncryptionPrivateKey_returnsEncryptionKey() throws Exception {
        when(keyCache.getUnchecked(encryptionKeyUri)).thenReturn(encryptionKey);

        PrivateKey retrievedKey = keyStore.getEncryptionPrivateKey();

        assertThat(retrievedKey).isEqualTo(encryptionKey);
    }
}
