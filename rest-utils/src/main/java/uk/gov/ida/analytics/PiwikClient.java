package uk.gov.ida.analytics;

import com.google.inject.Inject;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.client.Client;
import java.net.URI;

public class PiwikClient {

    private Client client;

    @Inject
    public PiwikClient(Client client){
        this.client = client;
    }

    public void report(URI uri, ContainerRequest request) {
        client.target(uri).request()
                .header("User-Agent", request.getRequestHeader("User-Agent"))
                .header("Accept-Language", request.getRequestHeader("Accept-Language"))
                .header("X-Forwarded-For", request.getRequestHeader("X-Forwarded-For"))
                .async()
                .get(String.class);
    }

}
