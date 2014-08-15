package uk.gov.ida.truststore;

public interface TrustStoreConfiguration {
    ClientTrustStoreConfiguration getClientTrustStoreConfiguration();
    ClientTrustStoreConfiguration getRpTrustStoreConfiguration();
}
