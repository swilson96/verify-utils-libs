package uk.gov.ida.restclient;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import java.util.Map;

import static uk.gov.ida.restclient.IdaClientBuilder.anIdaJerseyClientWithScheme;
import static uk.gov.ida.restclient.InsecureSSLSchemeRegistryBuilder.aConfigWithInsecureSSLSchemeRegistry;
import static uk.gov.ida.restclient.SecureSSLSchemeRegistryBuilder.aConfigWithSecureSSLSchemeRegistry;

public abstract class BaseClientProvider implements Provider<Client> {

    private final Client client;

    public BaseClientProvider(
            Environment environment,
            boolean doesAcceptSelfSignedCerts,
            JerseyClientConfiguration jerseyClientConfiguration,
            Optional<KeyStore> idaTrustStore,
            boolean enableStaleConnectionCheck,
            boolean retryTimeOutExceptions,
            String clientName) {

        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler(0, false);
        if (retryTimeOutExceptions) {
            retryHandler = new TimeoutRequestRetryHandler(jerseyClientConfiguration.getRetries());
        }

        SSLContext sslContext = getSslContext();
        SchemeRegistry schemeRegistry;
        if (doesAcceptSelfSignedCerts) {
            schemeRegistry = aConfigWithInsecureSSLSchemeRegistry(sslContext);
        } else {
            schemeRegistry = aConfigWithSecureSSLSchemeRegistry(sslContext,idaTrustStore);

        }
        Map<String, Object> configurationProperties = allowAllHostnameProperties(sslContext);

        client = anIdaJerseyClientWithScheme(
                environment,
                jerseyClientConfiguration,
                schemeRegistry,
                configurationProperties,
                clientName,
                enableStaleConnectionCheck,
                retryHandler
        );
    }

    private Map<String, Object> allowAllHostnameProperties(SSLContext sslContext) {
        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        return ImmutableMap.<String, Object>of(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
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
