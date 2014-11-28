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
            client.resource(uri).get(ServiceNameDto.class);
            return HealthCheck.Result.healthy();
        } catch (UniformInterfaceException e) {
            // It looks like there are no guarantees that the ClientResponse within a UniformInterfaceException is closed, so we are going to try to close it every time
            try {
                e.getResponse().close();
            } catch (ClientHandlerException che) {
                return handleFail(uri, e);
            }
            return handleFail(uri, e);
        } catch (ClientHandlerException e) {
            return handleFail(uri, e);
        }
    }

    private Result handleFail(URI uri, Throwable exception) {
        String message = format("Dependent service at {0} gives {1}.", uri, exception.getMessage());
        LOG.info(message);
        return Result.unhealthy(message);
    }

    @Override
    public String getName() {
        return "Service: " + dependentServiceConfiguration.toBaseUri();
    }
}
