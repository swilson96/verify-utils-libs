package uk.gov.ida.common.shared.configuration;

public class PublicKeyConfigurationBuilder {

    private String keyUri = "public key";

    public static PublicKeyConfigurationBuilder aPublicKeyConfiguration() {
        return new PublicKeyConfigurationBuilder();
    }

    public PublicKeyConfiguration build() {
        return new TestPublicKeyConfiguration(keyUri);
    }

    public PublicKeyConfigurationBuilder withKeyUri(String uri) {
        this.keyUri = uri;
        return this;
    }

    private static class TestPublicKeyConfiguration extends PublicKeyConfiguration {
        private TestPublicKeyConfiguration(
                String keyUri) {

            this.keyUri = keyUri;

            this.keyName = "default-key-name";
        }
    }
}
