package uk.gov.ida.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.ServiceNameDto;
import uk.gov.ida.configuration.DependentServiceConfiguration;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.ida.configuration.DependentServiceConfigurationBuilder.aDependentServiceConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class DependentServiceHealthCheckTest {

    @Mock
    private Client client;
    @Mock
    private WebResource webResource;

    private DependentServiceHealthCheck dependentServiceHealthCheck;
    private DependentServiceConfiguration dependentServiceConfiguration;

    @Before
    public void setUp() throws Exception {
        dependentServiceConfiguration = aDependentServiceConfiguration().build();
        dependentServiceHealthCheck = new DependentServiceHealthCheck(client, dependentServiceConfiguration);
    }

    @Test
    public void check_shouldReturnHealthyWhenDependentServiceReturnsCorrectServiceName() throws Exception {
        when(client.resource(any(URI.class))).thenReturn(webResource);
        when(webResource.get(ServiceNameDto.class)).thenReturn(new ServiceNameDto(dependentServiceConfiguration.getServiceName()));

        HealthCheck.Result result = dependentServiceHealthCheck.check();

        assertThat(result).isEqualTo(HealthCheck.Result.healthy());
    }

    @Test
    public void check_shouldReturnUnhealthyWhenDependentServiceReturnsWrongServiceName() throws Exception {
        when(client.resource(any(URI.class))).thenReturn(webResource);
        String expectedServiceName = dependentServiceConfiguration.getServiceName();
        String actualServiceName = expectedServiceName + "foo";
        when(webResource.get(ServiceNameDto.class)).thenReturn(new ServiceNameDto(actualServiceName));

        HealthCheck.Result result = dependentServiceHealthCheck.check();

        String expectedErrorMessage = format(
            "Expected service name {0} but was {1}.",
            expectedServiceName,
            actualServiceName);
        assertThat(result).isEqualTo(HealthCheck.Result.unhealthy(expectedErrorMessage));
    }

    @Test
    public void check_shouldReturnUnhealthyWhenDependentServiceThrowsAUniformInterfaceException() throws Exception {
        when(client.resource(any(URI.class))).thenReturn(webResource);
        when(webResource.get(ServiceNameDto.class)).thenThrow(UniformInterfaceExceptionBuilder.aUniformInterfaceException().build());

        HealthCheck.Result result = dependentServiceHealthCheck.check();

        final URI uri = UriBuilder
            .fromUri(dependentServiceConfiguration.toBaseUri())
            .path("service-name")
            .build();
        String expectedErrorMessage = format(
            "Dependent service {0} at {1} gives Client response status: 500.",
            dependentServiceConfiguration.getServiceName(),
            uri);
        assertThat(result).isEqualTo(HealthCheck.Result.unhealthy(expectedErrorMessage));
    }

    @Test
    public void check_shouldReturnUnhealthyWhenDependentServiceThrowsAClientHandlerException() throws Exception {
        String exceptionMessage = "it all went wrong";
        when(client.resource(any(URI.class))).thenReturn(webResource);
        when(webResource.get(ServiceNameDto.class)).thenThrow(new ClientHandlerException(exceptionMessage));

        HealthCheck.Result result = dependentServiceHealthCheck.check();

        final URI uri = UriBuilder
            .fromUri(dependentServiceConfiguration.toBaseUri())
            .path("service-name")
            .build();
        String expectedErrorMessage = format(
            "Dependent service {0} at {1} gives {2}.",
            dependentServiceConfiguration.getServiceName(),
            uri,
            exceptionMessage);
        assertThat(result).isEqualTo(HealthCheck.Result.unhealthy(expectedErrorMessage));
    }
}
