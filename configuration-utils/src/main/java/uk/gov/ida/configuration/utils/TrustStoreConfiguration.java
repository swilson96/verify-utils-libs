package uk.gov.ida.configuration.utils;

public interface TrustStoreConfiguration {
    ClientTrustStoreConfiguration getClientTrustStoreConfiguration();
    ClientTrustStoreConfiguration getRpTrustStoreConfiguration();
}
