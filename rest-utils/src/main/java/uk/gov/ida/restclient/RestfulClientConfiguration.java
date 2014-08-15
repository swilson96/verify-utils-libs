package uk.gov.ida.restclient;

import io.dropwizard.client.JerseyClientConfiguration;
import uk.gov.ida.truststore.ClientTrustStoreConfiguration;

public interface RestfulClientConfiguration {
    boolean getEnableStaleConnectionCheck();

    boolean getEnableRetryTimeOutConnections();

    JerseyClientConfiguration getJerseyClientConfiguration();
    boolean doesAcceptSelfSignedCerts();

    ClientTrustStoreConfiguration getClientTrustStoreConfiguration();

    JerseyClientConfiguration getDependentServiceJerseyClientConfiguration();
}
