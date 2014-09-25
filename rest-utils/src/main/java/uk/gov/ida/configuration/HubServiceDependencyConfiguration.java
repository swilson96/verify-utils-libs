package uk.gov.ida.configuration;

import uk.gov.ida.common.ServiceInfoConfiguration;

public interface HubServiceDependencyConfiguration {
    DependentServiceConfiguration getHubPolicy();

    DependentServiceConfiguration getEventSink();

    DependentServiceConfiguration getSamlEngine();

    DependentServiceConfiguration getSamlSoapProxy();

    ExternalServiceConfiguration getSamlProxy();

    DependentServiceConfiguration getHubConfig();

    ServiceInfoConfiguration getServiceInfo();
}
