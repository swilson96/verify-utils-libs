package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.truststore.IdaTrustStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class SecureSSLSchemeRegistryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SecureSSLSchemeRegistryBuilder.class);
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static SchemeRegistry aConfigWithSecureSSLSchemeRegistry(
            SSLContext sslContext,
            IdaTrustStore idaTrustStore) {

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

        return schemeRegistry;
    }

    private static TrustManager[] getTrustManagers(IdaTrustStore idaTrustStore) {
        if (idaTrustStore.getKeyStore().isPresent()) {
            try {
                final TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(idaTrustStore.getKeyStore().get());
                return trustManagerFactory.getTrustManagers();
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                throw Throwables.propagate(e);
            }
        }
        return null;
    }
}
