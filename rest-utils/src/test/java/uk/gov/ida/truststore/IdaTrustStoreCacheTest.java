package uk.gov.ida.truststore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.truststore.ClientTrustStoreConfiguration;
import uk.gov.ida.truststore.ClientTrustStoreConfigurationBuilder;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdaTrustStoreCacheTest {

    @Mock
    private KeyStoreLoader keyStoreLoader;

    @Mock
    private KeyStore keyStore;

    private IdaTrustStoreCache idaTrustStoreCache;

    private ClientTrustStoreConfiguration configuration;


    @Before
    public void setUp(){
        idaTrustStoreCache = new IdaTrustStoreCache(keyStoreLoader);
        configuration = ClientTrustStoreConfigurationBuilder.aClientTrustStoreConfiguration().build();
    }

    @Test
    public void shouldLoadKeyStoreIfNotAlreadyLoaded() throws Exception {
        when(keyStoreLoader.load(configuration.getStoreUri(), configuration.getPassword())).thenReturn(keyStore);
        IdaTrustStore idaTrustStore = idaTrustStoreCache.get(configuration);
        assertThat(idaTrustStore.getKeyStore().get()).isEqualTo(keyStore);
    }

    @Test
    public void shouldOnlyHaveToLoadTheKeyStoreOnce() throws Exception {
        when(keyStoreLoader.load(configuration.getStoreUri(), configuration.getPassword())).thenReturn(keyStore);
        idaTrustStoreCache.get(configuration);
        idaTrustStoreCache.get(configuration);
        verify(keyStoreLoader, times(1)).load(configuration.getStoreUri(), configuration.getPassword());
    }
}
