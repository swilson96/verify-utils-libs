package uk.gov.ida.healthcheck;

import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.ServiceNameDto;
import uk.gov.ida.configuration.DependentServiceConfiguration;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static java.text.MessageFormat.format;

public class DependentServiceHealthCheck extends InjectableHealthCheck {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DependentServiceHealthCheck.class);


    private final DependentServiceConfiguration dependentServiceConfiguration;
    private final Client client;

    public DependentServiceHealthCheck(Client client, DependentServiceConfiguration dependentServiceConfiguration) {
        this.client = client;
        this.dependentServiceConfiguration = dependentServiceConfiguration;
    }

    @Override
    protected HealthCheck.Result check() {
        UriBuilder uriBuilder = UriBuilder.fromUri(dependentServiceConfiguration.toBaseUri());
        if (StringUtils.isNotEmpty(dependentServiceConfiguration.getPathPrefix())) {
            uriBuilder.path(dependentServiceConfiguration.getPathPrefix());
        }
        final URI uri = uriBuilder.path("service-name").build();
        try {
            ServiceNameDto serviceNameDto = client.resource(uri).get(ServiceNameDto.class);
            if (!serviceNameDto.getServiceName().equals(dependentServiceConfiguration.getServiceName())) {
                String message = format("Expected service name {0} but was {1}.", dependentServiceConfiguration.getServiceName(), serviceNameDto.getServiceName());
                return HealthCheck.Result.unhealthy(message);
            } else {
                return HealthCheck.Result.healthy();
            }
        } catch (UniformInterfaceException | ClientHandlerException e) {
            String message = format("Dependent service {0} at {1} gives {2}.", dependentServiceConfiguration.getServiceName(), uri, e.getMessage());
            LOG.info(message);
            return HealthCheck.Result.unhealthy(message);
        }
    }

    @Override
    public String getName() {
        return "Service: " + dependentServiceConfiguration.getServiceName();
    }
}
