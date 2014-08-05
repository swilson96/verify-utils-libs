package uk.gov.ida.configuration.utils;


public class ExternalServiceConfigurationBuilder {

    private int port = 8080;
    private String scheme = "http";
    private String host = "localhost";

    public static ExternalServiceConfigurationBuilder anExternalServiceConfiguration() {
        return new ExternalServiceConfigurationBuilder();
    }

    public ExternalServiceConfiguration build() {
        return new TestExternalServiceConfiguration(
                host,
                scheme,
                port);
    }

    public ExternalServiceConfigurationBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public ExternalServiceConfigurationBuilder withScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public ExternalServiceConfigurationBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    private static class TestExternalServiceConfiguration extends ExternalServiceConfiguration {
        private TestExternalServiceConfiguration(
                String host,
                String scheme,
                int port) {

            this.host = host;
            this.scheme = scheme;
            this.port = port;
        }
    }

}
