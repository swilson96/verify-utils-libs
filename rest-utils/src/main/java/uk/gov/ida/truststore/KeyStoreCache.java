package uk.gov.ida.truststore;


import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.KeyStore;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Singleton
public class KeyStoreCache {


    private final Cache<String, Optional<KeyStore>> trustStoreCache;
    private final KeyStoreLoader keyStoreLoader;
    @Inject
    public KeyStoreCache(KeyStoreLoader keyStoreLoader) {
        this.keyStoreLoader = keyStoreLoader;
        trustStoreCache = CacheBuilder.newBuilder().build();
    }

    public Optional<KeyStore> get(final ClientTrustStoreConfiguration configuration) {

        final String storeUri = configuration.getStoreUri();
        final String password = configuration.getPassword();

        try {
            return trustStoreCache.get(storeUri, new Callable<Optional<KeyStore>>() {
                @Override
                public Optional<KeyStore> call() throws Exception {
                    KeyStore ks = keyStoreLoader.load(storeUri, password);
                    return Optional.fromNullable(ks);
                }
            });
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

}
