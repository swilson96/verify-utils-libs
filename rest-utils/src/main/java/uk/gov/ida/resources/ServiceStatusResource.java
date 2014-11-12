package uk.gov.ida.resources;

import com.google.inject.Inject;
import org.apache.http.HttpStatus;
import uk.gov.ida.common.CommonUrls;
import uk.gov.ida.configuration.ServerStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(CommonUrls.SERVICE_STATUS)
public class ServiceStatusResource {

    private ServerStatus serverStatus;

    @Inject
    public ServiceStatusResource() {

        this.serverStatus = ServerStatus.getInstance();
    }

    @GET
    public Response isOnline(){
        if (serverStatus.isShutdownSequenceStarted()){
            return Response.noContent().status(HttpStatus.SC_SERVICE_UNAVAILABLE).build();
        } else {
            return Response.ok().build();
        }
    }


}

