package uk.gov.ida.configuration;

import uk.gov.ida.common.ServiceInfo;

public interface HubServiceDependencyConfiguration {
    DependentServiceConfiguration getFrontend();

    DependentServiceConfiguration getHubPolicy();

    DependentServiceConfiguration getEventSink();

    DependentServiceConfiguration getSamlEngine();

    DependentServiceConfiguration getSamlSoapProxy();

    ExternalServiceConfiguration getSamlProxy();

    DependentServiceConfiguration getHubConfig();

    ServiceInfo getServiceInfo();
}
