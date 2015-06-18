package uk.gov.ida.analytics;

import com.google.inject.Inject;
import com.sun.jersey.api.core.HttpRequestContext;

import javax.ws.rs.client.Client;
import java.net.URI;

public class PiwikClient {

    private Client client;

    @Inject
    public PiwikClient(Client client){
        this.client = client;
    }

    public void report(URI uri, HttpRequestContext request) {
        client.target(uri).request()
                .header("User-Agent", request.getHeaderValue("User-Agent"))
                .header("Accept-Language", request.getHeaderValue("Accept-Language"))
                .header("X-Forwarded-For", request.getHeaderValue("X-Forwarded-For"))
                .async()
                .get(String.class);
    }

}
