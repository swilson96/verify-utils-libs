package uk.gov.ida.jerseyclient;

import io.dropwizard.setup.Environment;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;

import javax.inject.Inject;

public class ClientProvider extends BaseClientProvider {

    @Inject
    public ClientProvider(
            Environment environment,
            RestfulClientConfiguration baseConfiguration) {

        super(
                environment,
                baseConfiguration.getJerseyClientConfiguration(),
                baseConfiguration.getEnableRetryTimeOutConnections(),
                "MicroServiceClient"
        );
    }
}
