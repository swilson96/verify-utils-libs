package uk.gov.ida.configuration;

import uk.gov.ida.common.ServiceInfoConfiguration;

public interface HubServiceDependencyConfiguration {
    ExternalServiceConfiguration getFrontendExternal();

    DependentServiceConfiguration getHubPolicy();

    DependentServiceConfiguration getEventSink();

    DependentServiceConfiguration getSamlEngine();

    DependentServiceConfiguration getSamlSoapProxy();

    DependentServiceConfiguration getHubSamlProxy();

    DependentServiceConfiguration getHubConfig();

    ServiceInfoConfiguration getServiceInfo();
}
