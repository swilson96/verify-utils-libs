package uk.gov.ida.configuration.utils;

import io.dropwizard.client.JerseyClientConfiguration;

public interface RestfulClientConfiguration {
    boolean getEnableStaleConnectionCheck();

    boolean getEnableRetryTimeOutConnections();

    JerseyClientConfiguration getJerseyClientConfiguration();
    boolean doesAcceptSelfSignedCerts();

    ClientTrustStoreConfiguration getClientTrustStoreConfiguration();

    JerseyClientConfiguration getDependentServiceJerseyClientConfiguration();
}
