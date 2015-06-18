package uk.gov.ida.restclient;

import com.google.inject.Provider;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import javax.validation.Validation;
import javax.ws.rs.client.Client;
import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public abstract class BaseClientProvider implements Provider<Client> {

    private final Client client;

    public BaseClientProvider(
            Environment environment,
            boolean doesAcceptSelfSignedCerts,
            JerseyClientConfiguration jerseyClientConfiguration,
            KeyStore trustStore,
            boolean enableStaleConnectionCheck,
            boolean enableRetryTimeOutConnections,
            String clientName,
            X509HostnameVerifier hostnameVerifier) {

        JerseyClientBuilder jerseyClientBuilder = new JerseyClientBuilder(environment)
                .using(jerseyClientConfiguration)
                .using(getHttpRequestRetryHandler(jerseyClientConfiguration, enableRetryTimeOutConnections))
                .using(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .using(getConnectionSocketFactoryRegistry(doesAcceptSelfSignedCerts, trustStore, hostnameVerifier));

        HttpClientBuilder httpClientBuilder = new HttpClientBuilder(environment).name(clientName);
        jerseyClientBuilder.setApacheHttpClientBuilder(httpClientBuilder);
        jerseyClientBuilder.withProvider(new JacksonMessageBodyProvider(environment.getObjectMapper(), Validation.buildDefaultValidatorFactory().getValidator()));

        // There used to be a property ApacheHttpClient4Config.PROPERTY_ENABLE_BUFFERING set but this appears to be gone in dropwizard 8

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

    private Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry(boolean doesAcceptSelfSignedCerts, KeyStore trustStore, X509HostnameVerifier hostnameVerifier) {
        try {
            SSLContextBuilder sslcontext = SSLContexts.custom();
            if (!doesAcceptSelfSignedCerts) {
                sslcontext = sslcontext.loadTrustMaterial(trustStore);
            }
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext.build(), new String[]{"TLSv1.2"}, null, hostnameVerifier);
            return RegistryBuilder.<ConnectionSocketFactory>create().register("https", sslConnectionSocketFactory).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Client get() {
        return client;
    }
}
