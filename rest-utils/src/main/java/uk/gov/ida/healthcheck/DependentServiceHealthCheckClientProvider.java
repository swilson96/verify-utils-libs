package uk.gov.ida.healthcheck;

import com.google.common.base.Optional;
import io.dropwizard.setup.Environment;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;

import java.security.KeyStore;

public class DependentServiceHealthCheckClientProvider extends BaseClientProvider {
    public DependentServiceHealthCheckClientProvider(
            final Environment environment,
            final RestfulClientConfiguration configuration,
            final KeyStore trustStore) {

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
