package uk.gov.ida.restclient;

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
import uk.gov.ida.truststore.IdaTrustStore;

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
            IdaTrustStore idaTrustStore,
            boolean enableStaleConnectionCheck,
            boolean retryTimeOutExceptions,
            String clientName) {

        HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler(0, false);
        if (retryTimeOutExceptions) {
            retryHandler = new TimeoutRequestRetryHandler(jerseyClientConfiguration.getRetries());
        }

        SSLClientConfiguration sslClientConfiguration;
        if (doesAcceptSelfSignedCerts) {
            sslClientConfiguration = getInsecureSSLClientConfiguration();
        } else {
            sslClientConfiguration = getSecureSSLClientConfiguration(idaTrustStore);
        }
        client = anIdaJerseyClientWithScheme(
                environment,
                jerseyClientConfiguration,
                sslClientConfiguration.getSchemeRegistry(),
                sslClientConfiguration.getConfigurationProperties(),
                clientName,
                enableStaleConnectionCheck,
                retryHandler
        );
    }

    private SSLClientConfiguration getInsecureSSLClientConfiguration() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        SchemeRegistry schemeRegistry = aConfigWithInsecureSSLSchemeRegistry(
                sslContext
        );

        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        Map<String, Object> configurationProperties = ImmutableMap.<String, Object>of(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
        return new SSLClientConfiguration(schemeRegistry, configurationProperties);
    }

    private SSLClientConfiguration getSecureSSLClientConfiguration(IdaTrustStore idaTrustStore) {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        SchemeRegistry schemeRegistry = aConfigWithSecureSSLSchemeRegistry(
                sslContext,
                idaTrustStore
        );

        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        Map<String, Object> configurationProperties = ImmutableMap.<String, Object>of(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
        return new SSLClientConfiguration(schemeRegistry, configurationProperties);
    }

    @Override
    public Client get() {
        return client;
    }
}
