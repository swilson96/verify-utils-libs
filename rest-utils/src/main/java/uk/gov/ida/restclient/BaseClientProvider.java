package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import javax.inject.Provider;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class BaseClientProvider implements Provider<Client> {

    private final Client client;

    public BaseClientProvider(
            Environment environment,
            boolean doesAcceptSelfSignedCerts,
            JerseyClientConfiguration jerseyClientConfiguration,
            KeyStore trustStore,
            boolean enableRetryTimeOutConnections,
            String clientName,
            X509HostnameVerifier hostnameVerifier) {

        JerseyClientBuilder jerseyClientBuilder = new JerseyClientBuilder(environment)
                .using(jerseyClientConfiguration)
                .using(getHttpRequestRetryHandler(jerseyClientConfiguration, enableRetryTimeOutConnections))
                .using(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .using(getConnectionSocketFactoryRegistry(doesAcceptSelfSignedCerts, trustStore, hostnameVerifier));

        client = jerseyClientBuilder.build(clientName);
    }

    static TrustManager[] getTrustManagers(KeyStore trustStore) {
        try {
            final TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            return trustManagerFactory.getTrustManagers();
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw Throwables.propagate(e);
        }
    }

    private HttpRequestRetryHandler getHttpRequestRetryHandler(JerseyClientConfiguration jerseyClientConfiguration, boolean enableRetryTimeOutConnections) {
        HttpRequestRetryHandler retryHandler;
        if (enableRetryTimeOutConnections) {
            retryHandler = new TimeoutRequestRetryHandler(jerseyClientConfiguration.getRetries());
        } else {
            retryHandler = new StandardHttpRequestRetryHandler(0, false);
        }
        return retryHandler;
    }

    private Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(boolean doesAcceptSelfSignedCerts, KeyStore trustStore, X509HostnameVerifier hostnameVerifier) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            if(doesAcceptSelfSignedCerts) {
                sslContext.init(null, new TrustManager[]{new InsecureTrustManager()}, new SecureRandom());
            } else {
                sslContext.init(null, getTrustManagers(trustStore), new SecureRandom());
            }
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslConnectionSocketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client get() {
        return client;
    }
}
