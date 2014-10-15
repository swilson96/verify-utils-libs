package uk.gov.ida.restclient;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

import java.util.Map;

import static uk.gov.ida.restclient.IdaClientBuilder.anIdaJerseyClientWithScheme;
import static uk.gov.ida.restclient.InsecureSSLSchemeRegistryBuilder.aConfigWithInsecureSSLSchemeRegistry;


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
        SSLClientConfiguration sslClientConfiguration = getInsecureSSLClientConfiguration();
        return anIdaJerseyClientWithScheme(
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
            LOG.error("Error creating SSL context.", e);
            throw Throwables.propagate(e);
        }
        SchemeRegistry schemeRegistry = aConfigWithInsecureSSLSchemeRegistry(
                sslContext
        );

        HTTPSProperties httpsProperties = new HTTPSProperties(new AllowAllHostnameVerifier(), sslContext);
        Map<String, Object> configurationProperties = ImmutableMap.<String, Object>of(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);
        return new SSLClientConfiguration(schemeRegistry, configurationProperties);
    }
}
