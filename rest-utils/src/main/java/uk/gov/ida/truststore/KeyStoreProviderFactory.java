package uk.gov.ida.truststore;

/**
 * Why does this class exist? Why indeed. It's to support testing of the
 * {@link uk.gov.ida.shared.rest.healthcheck.DependentServiceHealthCheckBundle}. The
 * {@link uk.gov.ida.truststore.KeyStoreProvider} needs to be mocked so that it does
 * not try and read from the file system when running the test. To enable this, the
 * bundle can't be injected with the provider directly, since the provider depends on
 * the {@link uk.gov.ida.truststore.ClientTrustStoreConfiguration}, which the bundle only has once inside its
 * {@link uk.gov.ida.shared.rest.healthcheck.DependentServiceHealthCheckBundle#run(uk.gov.ida.shared.configuration.BaseConfiguration, io.dropwizard.setup.Environment)}
 * method.
 */
public class KeyStoreProviderFactory {
    public KeyStoreProvider create(ClientTrustStoreConfiguration clientTrustStoreConfiguration) {
        return new KeyStoreProvider(clientTrustStoreConfiguration, new KeyStoreLoader());
    }
}