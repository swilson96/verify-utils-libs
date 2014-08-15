package uk.gov.ida.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.configuration.DependentServiceConfiguration;
import uk.gov.ida.restclient.RestfulClientConfiguration;
import uk.gov.ida.truststore.ClientTrustStoreConfiguration;
import uk.gov.ida.truststore.IdaTrustStoreProvider;
import uk.gov.ida.truststore.IdaTrustStoreProviderFactory;

import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.configuration.DependentServiceConfigurationBuilder.aDependentServiceConfiguration;
import static uk.gov.ida.jerseyclient.JerseyClientConfigurationBuilder.aJerseyClientConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class DependentServiceHealthCheckBundleTest {

    @Mock(answer = RETURNS_MOCKS)
    private Environment environment;
    @Mock
    private IdaTrustStoreProviderFactory idaTrustStoreProviderFactory;

    private DependentServiceHealthCheckBundle bundle;

    @Before
    public void setUp() throws Exception {
        bundle = new DependentServiceHealthCheckBundle(idaTrustStoreProviderFactory);
    }

    @Test
    public void run_shouldAddDependentServiceHealthChecksForAllDependentServiceConfigurations() throws Exception {
        when(idaTrustStoreProviderFactory.create(any(ClientTrustStoreConfiguration.class)))
                .thenReturn(mock(IdaTrustStoreProvider.class));

        HealthCheckRegistry healthCheckRegistry = mock(HealthCheckRegistry.class);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);

        TestConfiguration configuration = new TestConfiguration();

        bundle.run(configuration, environment);

        verify(healthCheckRegistry, times(2)).register(anyString(), any(HealthCheck.class));
    }

    @Test
    public void run_shouldDoNothingWhenServiceDoesNotDependOnAnyOthers() throws Exception {
        TestConfigurationWithNoDependentServices configuration = new TestConfigurationWithNoDependentServices();

        HealthCheckRegistry healthCheckRegistry = mock(HealthCheckRegistry.class);
        when(environment.healthChecks()).thenReturn(healthCheckRegistry);

        bundle.run(configuration, environment);

        verify(healthCheckRegistry, never()).register(anyString(), any(HealthCheck.class));
    }

    private class TestConfiguration implements RestfulClientConfiguration {
        @SuppressWarnings("unused")
        public int shouldNotCount() {
            return 1;
        }

        @Override
        public boolean getEnableStaleConnectionCheck() {
            return false;
        }

        @Override
        public boolean getEnableRetryTimeOutConnections() {
            return false;
        }

        @Override
        public ClientTrustStoreConfiguration getClientTrustStoreConfiguration() {
            return null;
        }

        @Override
        public JerseyClientConfiguration getDependentServiceJerseyClientConfiguration() {
            return getJerseyClientConfiguration();
        }

        @SuppressWarnings("unused")
        public DependentServiceConfiguration thisIsANonNullConfig() {
            return aDependentServiceConfiguration().build();
        }

        @SuppressWarnings("unused")
        public DependentServiceConfiguration thisIsANullConfigSoShouldNotCount() {
            return null;
        }

        @SuppressWarnings("unused")
        public DependentServiceConfiguration thisIsASecondNonNullConfig() {
            return aDependentServiceConfiguration().build();
        }

        @Override
        public JerseyClientConfiguration getJerseyClientConfiguration() {
            return aJerseyClientConfiguration().build();
        }

        @Override
        public boolean doesAcceptSelfSignedCerts() {
            return true;
        }
    }

    private class TestConfigurationWithNoDependentServices implements RestfulClientConfiguration {
        @Override
        public boolean getEnableStaleConnectionCheck() {
            return false;
        }

        @Override
        public boolean getEnableRetryTimeOutConnections() {
            return false;
        }

        @Override
        public JerseyClientConfiguration getJerseyClientConfiguration() {
            return null;
        }

        @Override
        public ClientTrustStoreConfiguration getClientTrustStoreConfiguration() {
            return null;
        }

        @Override
        public JerseyClientConfiguration getDependentServiceJerseyClientConfiguration() {
            return null;
        }

        @Override
        public boolean doesAcceptSelfSignedCerts() {
            return true;
        }
    }
}
