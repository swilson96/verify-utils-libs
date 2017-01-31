package uk.gov.ida.truststore;

public class ClientTrustStoreConfigurationBuilder {

    public static ClientTrustStoreConfigurationBuilder aClientTrustStoreConfiguration() {
        return new ClientTrustStoreConfigurationBuilder();
    }

    public ClientTrustStoreConfiguration build(){
        return new TestClientTrustStoreConfiguration("storeFile", "password");
    }

    private class TestClientTrustStoreConfiguration extends ClientTrustStoreConfiguration{
        private TestClientTrustStoreConfiguration(String storeFile, String password) {
            this.storeFile = storeFile;
            this.password = password;
        }

    }
}
