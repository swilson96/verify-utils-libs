package uk.gov.ida.restclient;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

public abstract class SecureSSLClientConfigurationBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SecureSSLClientConfigurationBuilder.class);
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static SecureSSLClientConfiguration aConfigWithSecureSSLSchemeRegistry(
            SSLContext sslContext,
            Optional<KeyStore> idaTrustStore) {

        final TrustManager[] trustManagers = getTrustManagers(idaTrustStore);
        try {
            sslContext.init(null, trustManagers, SECURE_RANDOM);
        } catch (KeyManagementException e){
            LOG.error("Error when trying to create SSL.", e);
            throw Throwables.propagate(e);
        }

        final Scheme https = new Scheme("https", 443, new SSLSocketFactory(sslContext));
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(https);

        return new SecureSSLClientConfiguration(schemeRegistry, new HashMap<String, Object>());
    }

    private static TrustManager[] getTrustManagers(Optional<KeyStore> idaTrustStore) {
        if (idaTrustStore.isPresent()) {
            try {
                final TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(idaTrustStore.get());
                return trustManagerFactory.getTrustManagers();
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                throw Throwables.propagate(e);
            }
        }
        return null;
    }
}
