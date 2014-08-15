package uk.gov.ida.healthcheck;

import io.dropwizard.setup.Environment;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;
import uk.gov.ida.truststore.IdaTrustStore;

public class DependentServiceHealthCheckClientProvider extends BaseClientProvider {
    public DependentServiceHealthCheckClientProvider(
            final Environment environment,
            final RestfulClientConfiguration configuration,
            final IdaTrustStore trustStore) {

        super(
                environment,
                configuration.doesAcceptSelfSignedCerts(),
                configuration.getDependentServiceJerseyClientConfiguration(),
                trustStore,
                configuration.getEnableStaleConnectionCheck(),
                configuration.getEnableRetryTimeOutConnections(),
                "DependentServiceHealthCheckClient"
        );
    }
}
