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
            JerseyClientConfiguration jerseyClientConfiguration,
            boolean enableRetryTimeOutConnections,
            String clientName) {

        JerseyClientBuilder jerseyClientBuilder = new JerseyClientBuilder(environment)
                .using(jerseyClientConfiguration)
                .using(getHttpRequestRetryHandler(jerseyClientConfiguration, enableRetryTimeOutConnections))
                .using(new SystemDefaultRoutePlanner(ProxySelector.getDefault()));

        client = jerseyClientBuilder.build(clientName);
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

    @Override
    public Client get() {
        return client;
    }
}
