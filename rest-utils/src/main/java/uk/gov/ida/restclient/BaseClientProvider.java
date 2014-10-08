package uk.gov.ida.restclient;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.sun.jersey.api.client.Client;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;

import java.security.KeyStore;

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

        if (doesAcceptSelfSignedCerts) {
            client = new IgnoreSSLJerseyClientBuilder(environment, jerseyClientConfiguration, enableStaleConnectionCheck, retryHandler).build(clientName);
        } else {
            client = new SecureSSLJerseyClientBuilder(environment, jerseyClientConfiguration, idaTrustStore, enableStaleConnectionCheck, retryHandler).build(clientName);
        }
    }

    @Override
    public Client get() {
        return client;
    }
}
