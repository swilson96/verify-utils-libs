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

import javax.net.ssl.SSLContext;

import static uk.gov.ida.restclient.IdaClientBuilder.anIdaJerseyClientWithScheme;
import static uk.gov.ida.restclient.InsecureSSLClientConfigurationBuilder.aConfigWithInsecureSSLSchemeRegistry;


public class IgnoreSSLJerseyClientBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(IgnoreSSLJerseyClientBuilder.class);

    private final Environment environment;
    private final JerseyClientConfiguration jerseyClientConfiguration;
    private boolean enableStaleConnectionCheck;
    private final HttpRequestRetryHandler retryHandler;

    public IgnoreSSLJerseyClientBuilder(
            Environment environment,
            JerseyClientConfiguration jerseyClientConfiguration,
            boolean enableStaleConnectionCheck,
            HttpRequestRetryHandler retryHandler) {
        this.environment = environment;
        this.jerseyClientConfiguration = jerseyClientConfiguration;
        this.enableStaleConnectionCheck = enableStaleConnectionCheck;
        this.retryHandler = retryHandler;
    }

    public Client build(String clientName) {
        InsecureSSLClientConfiguration insecureSSLClientConfiguration = getInsecureSSLClientConfiguration();
        return anIdaJerseyClientWithScheme(
            environment,
            jerseyClientConfiguration,
            insecureSSLClientConfiguration.getSchemeRegistry(),
            insecureSSLClientConfiguration.getConfigurationProperties(),
            clientName,
            enableStaleConnectionCheck,
            retryHandler
        );
    }

    private InsecureSSLClientConfiguration getInsecureSSLClientConfiguration() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (Exception e) {
            LOG.error("Error creating SSL context.", e);
            throw Throwables.propagate(e);
        }
        InsecureSSLClientConfiguration insecureSSLClientConfiguration = aConfigWithInsecureSSLSchemeRegistry(
                sslContext
        );

        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        insecureSSLClientConfiguration.addProperty(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
        return insecureSSLClientConfiguration;
    }
}
