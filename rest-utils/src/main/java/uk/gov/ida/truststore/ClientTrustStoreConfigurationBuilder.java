package uk.gov.ida.truststore;

public class ClientTrustStoreConfigurationBuilder {

    public static ClientTrustStoreConfigurationBuilder aClientTrustStoreConfiguration() {
        return new ClientTrustStoreConfigurationBuilder();
    }

    public ClientTrustStoreConfiguration build(){
        return new TestClientTrustStoreConfiguration("storeUri", "password");
    }

    private class TestClientTrustStoreConfiguration extends ClientTrustStoreConfiguration{
        private TestClientTrustStoreConfiguration(String storeUri, String password) {
            this.storeUri = storeUri;
            this.password = password;
        }

    }
}
