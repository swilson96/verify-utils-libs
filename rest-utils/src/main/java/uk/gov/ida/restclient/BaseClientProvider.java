package uk.gov.ida.restclient;

import com.google.inject.Provider;
import com.sun.jersey.api.client.Client;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import uk.gov.ida.truststore.IdaTrustStore;

public abstract class BaseClientProvider implements Provider<Client> {

    private final Client client;

    public BaseClientProvider(
            Environment environment,
            boolean doesAcceptSelfSignedCerts,
            JerseyClientConfiguration jerseyClientConfiguration,
            IdaTrustStore idaTrustStore,
            boolean enableStaleConnectionCheck,
            String clientName) {

        if (doesAcceptSelfSignedCerts) {
            client = new IgnoreSSLJerseyClientBuilder(environment, jerseyClientConfiguration, enableStaleConnectionCheck).build(clientName);
        } else {
            client = new SecureSSLJerseyClientBuilder(environment, jerseyClientConfiguration, idaTrustStore, enableStaleConnectionCheck).build(clientName);
        }
    }

    @Override
    public Client get() {
        return client;
    }
}
