package uk.gov.ida.configuration;

public class DependentServiceConfigurationBuilder {

    public static DependentServiceConfigurationBuilder aDependentServiceConfiguration() {
        return new DependentServiceConfigurationBuilder();
    }

    public DependentServiceConfiguration build() {
        return new TestDependentServiceConfiguration(
                "localhost",
                "http",
                8080,
                "You're dependent on me!");
    }

    private static class TestDependentServiceConfiguration extends DependentServiceConfiguration {
        private TestDependentServiceConfiguration(
                String host,
                String scheme,
                int port,
                String serviceName) {

            this.host = host;
            this.scheme = scheme;
            this.port = port;
            this.serviceName = serviceName;
        }
    }
}
