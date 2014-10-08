package uk.gov.ida.healthcheck;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.configuration.DependentServiceConfiguration;
import uk.gov.ida.restclient.BaseClientProvider;
import uk.gov.ida.restclient.RestfulClientConfiguration;
import uk.gov.ida.truststore.KeyStoreProvider;
import uk.gov.ida.truststore.KeyStoreProviderFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class DependentServiceHealthCheckBundle implements ConfiguredBundle<RestfulClientConfiguration> {

    private KeyStoreProviderFactory trustStoreProviderFactory;

    @Inject
    public DependentServiceHealthCheckBundle(
            KeyStoreProviderFactory trustStoreProviderFactory) {

        this.trustStoreProviderFactory = trustStoreProviderFactory;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(
            final RestfulClientConfiguration configuration,
            final Environment environment)
            throws IllegalAccessException, InvocationTargetException {

        if (configuration.getJerseyClientConfiguration() == null) {
            return;
        }

        addHealthCheckForEachDependentService(configuration, environment);
    }

    private void addHealthCheckForEachDependentService(
            final RestfulClientConfiguration configuration,
            final Environment environment)
            throws InvocationTargetException, IllegalAccessException {

        Client client = getClient(configuration, environment);

        final List<DependentServiceConfiguration> dependentServices =
                getDependentServices(configuration);

        if (!dependentServices.isEmpty()) {
            for (DependentServiceConfiguration service : dependentServices) {
                DependentServiceHealthCheck dependentServiceHealthCheck = new DependentServiceHealthCheck(client, service);
                environment.healthChecks().register(dependentServiceHealthCheck.getName(), dependentServiceHealthCheck);
            }
        }
    }

    private Client getClient(
            final RestfulClientConfiguration configuration,
            final Environment environment) {

        final Optional<KeyStore> trustStore = getTrustStore(configuration);
        BaseClientProvider clientProvider = new DependentServiceHealthCheckClientProvider(
                environment,
                configuration,
                trustStore
        );
        return clientProvider.get();
    }

    private Optional<KeyStore> getTrustStore(final RestfulClientConfiguration configuration) {
        final KeyStoreProvider idaTrustStoreProvider =
                trustStoreProviderFactory.create(configuration.getClientTrustStoreConfiguration());
        return idaTrustStoreProvider.get();
    }

    private List<DependentServiceConfiguration> getDependentServices(
            final RestfulClientConfiguration configuration)
            throws InvocationTargetException, IllegalAccessException {

        List<Method> dependentServiceConfigurationRetrievers =
                getDependentServiceConfigurationRetrievers(configuration);
        return getDependentServiceConfigurations(
                configuration,
                dependentServiceConfigurationRetrievers
        );
    }

    private List<DependentServiceConfiguration> getDependentServiceConfigurations(final RestfulClientConfiguration configuration, final List<Method> dependentServiceConfigurationRetrievers) throws IllegalAccessException, InvocationTargetException {
        List<DependentServiceConfiguration> configurations = new ArrayList<>();
        for (Method method : dependentServiceConfigurationRetrievers) {
            final DependentServiceConfiguration dependentServiceConfiguration = (DependentServiceConfiguration)
                    method.invoke(configuration);
            if (dependentServiceConfiguration != null) {
                configurations.add(dependentServiceConfiguration);
            }
        }
        return configurations;
    }

    private List<Method> getDependentServiceConfigurationRetrievers(final RestfulClientConfiguration configuration) {
        List<Method> dependentServiceConfigurationRetrievers = new ArrayList<>();
        for (Method method : configuration.getClass().getMethods()) {
            if (DependentServiceConfiguration.class.isAssignableFrom(method.getReturnType())) {
                dependentServiceConfigurationRetrievers.add(method);
            }
        }
        return dependentServiceConfigurationRetrievers;
    }
}
