package uk.gov.ida.jerseyclient;

import com.google.inject.Inject;
import io.dropwizard.setup.Environment;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;

import java.security.KeyStore;

public class ClientProvider extends BaseClientProvider {

    @Inject
    public ClientProvider(
            Environment environment,
            RestfulClientConfiguration baseConfiguration,
            KeyStore trustStore) {

        super(
                environment,
                baseConfiguration.doesAcceptSelfSignedCerts(),
                baseConfiguration.getJerseyClientConfiguration(),
                trustStore,
                baseConfiguration.getEnableRetryTimeOutConnections(),
                "MicroServiceClient",
                new AllowAllHostnameVerifier()
        );
    }
}
