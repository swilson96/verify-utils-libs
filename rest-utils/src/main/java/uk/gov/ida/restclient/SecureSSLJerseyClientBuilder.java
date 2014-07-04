package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.truststore.IdaTrustStore;

import javax.net.ssl.SSLContext;

import static uk.gov.ida.restclient.IdaClientBuilder.anIdaJerseyClientWithScheme;
import static uk.gov.ida.restclient.SecureSSLClientConfigurationBuilder.aConfigWithSecureSSLSchemeRegistry;


public class SecureSSLJerseyClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SecureSSLJerseyClientBuilder.class);

    private final Environment environment;
    private final JerseyClientConfiguration jerseyClientConfiguration;
    private final IdaTrustStore idaTrustStore;
    private boolean enableStaleConnectionCheck;
    private final HttpRequestRetryHandler retryHandler;

    public SecureSSLJerseyClientBuilder(
            Environment environment,
            JerseyClientConfiguration jerseyClientConfiguration,
            IdaTrustStore idaTrustStore,
            boolean enableStaleConnectionCheck,
            HttpRequestRetryHandler retryHandler) {

        this.environment = environment;
        this.jerseyClientConfiguration = jerseyClientConfiguration;
        this.idaTrustStore = idaTrustStore;
        this.enableStaleConnectionCheck = enableStaleConnectionCheck;
        this.retryHandler = retryHandler;
    }

    public Client build(String clientName) {
        SecureSSLClientConfiguration secureSSLClientConfiguration = getSecureSSLClientConfiguration();
        return anIdaJerseyClientWithScheme(
                environment,
                jerseyClientConfiguration,
                secureSSLClientConfiguration.getSchemeRegistry(),
                secureSSLClientConfiguration.getConfigurationProperties(),
                clientName,
                enableStaleConnectionCheck,
                retryHandler);
    }

    private SecureSSLClientConfiguration getSecureSSLClientConfiguration() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (Exception e) {
            LOG.error("Error creating SSL context.", e);
            throw Throwables.propagate(e);
        }
        SecureSSLClientConfiguration secureSSLClientConfiguration = aConfigWithSecureSSLSchemeRegistry(
                sslContext,
                idaTrustStore
        );

        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        secureSSLClientConfiguration.addProperty(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
        return secureSSLClientConfiguration;
    }
}
