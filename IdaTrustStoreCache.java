package uk.gov.ida.shared.rest.truststore;


import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.ida.truststore.ClientTrustStoreConfiguration;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.KeyStore;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Singleton
public class IdaTrustStoreCache {


    private final Cache<String, IdaTrustStore> trustStoreCache;
    private final KeyStoreLoader keyStoreLoader;
    @Inject
    public IdaTrustStoreCache(KeyStoreLoader keyStoreLoader) {
        this.keyStoreLoader = keyStoreLoader;
        trustStoreCache = CacheBuilder.newBuilder().build();
    }

    public IdaTrustStore get(final ClientTrustStoreConfiguration configuration) {

        final String storeUri = configuration.getStoreUri();
        final String password = configuration.getPassword();

        try {
            return trustStoreCache.get(storeUri, new Callable<IdaTrustStore>() {
                @Override
                public IdaTrustStore call() throws Exception {
                    KeyStore ks = keyStoreLoader.load(storeUri, password);

                    return new IdaTrustStore(ks);
                }
            });
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

}
