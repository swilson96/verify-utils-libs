package uk.gov.ida.jerseyclient;

import com.google.inject.Inject;
import io.dropwizard.setup.Environment;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;
import uk.gov.ida.truststore.IdaTrustStore;

public class ClientProvider extends BaseClientProvider {

    @Inject
    public ClientProvider(
            Environment environment,
            RestfulClientConfiguration baseConfiguration,
            IdaTrustStore idaTrustStore) {

        super(
                environment,
                baseConfiguration.doesAcceptSelfSignedCerts(),
                baseConfiguration.getJerseyClientConfiguration(),
                idaTrustStore,
                baseConfiguration.getEnableStaleConnectionCheck(),
                baseConfiguration.getEnableRetryTimeOutConnections(),
                "MicroServiceClient"
        );
    }
}
