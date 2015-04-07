package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import com.google.inject.Provider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

import static uk.gov.ida.restclient.InsecureSSLSchemeRegistryBuilder.aConfigWithInsecureSSLSchemeRegistry;
import static uk.gov.ida.restclient.SecureSSLSchemeRegistryBuilder.aConfigWithSecureSSLSchemeRegistry;

public abstract class BaseClientProvider implements Provider<Client> {

    private final Client client;

    public BaseClientProvider(
            Environment environment,
            boolean doesAcceptSelfSignedCerts,
            JerseyClientConfiguration jerseyClientConfiguration,
            KeyStore trustStore,
            boolean enableStaleConnectionCheck,
            boolean retryTimeOutExceptions,
            String clientName,
            X509HostnameVerifier hostnameVerifier) {

        HttpRequestRetryHandler retryHandler;
        if (retryTimeOutExceptions) {
            retryHandler = new TimeoutRequestRetryHandler(jerseyClientConfiguration.getRetries());
        } else {
            retryHandler = new StandardHttpRequestRetryHandler(0, false);
        }

        SSLContext sslContext = getSslContext();
        SchemeRegistry schemeRegistry;
        if (doesAcceptSelfSignedCerts) {
            schemeRegistry = aConfigWithInsecureSSLSchemeRegistry(sslContext, hostnameVerifier);
        } else {
            schemeRegistry = aConfigWithSecureSSLSchemeRegistry(sslContext, trustStore, hostnameVerifier);
        }

        client = new IdaJerseyClientBuilder(environment, enableStaleConnectionCheck)
                .using(jerseyClientConfiguration)
                .using(schemeRegistry)
                .using(retryHandler)
                .withProperty(ApacheHttpClient4Config.PROPERTY_ENABLE_BUFFERING, true)
                .build(clientName);
    }

    private SSLContext getSslContext() {
        try {
            return SSLContext.getInstance("TLSv1.2");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Client get() {
        return client;
    }
}
