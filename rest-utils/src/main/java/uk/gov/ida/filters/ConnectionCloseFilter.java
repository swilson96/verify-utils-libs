package uk.gov.ida.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import uk.gov.ida.configuration.ServiceStatus;

public class ConnectionCloseFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        ServiceStatus serviceStatus = ServiceStatus.getInstance();
        if (!serviceStatus.isServerStatusOK()) {
            response.getHttpHeaders().add("Connection", "close");
        }
        return response;
    }
}
