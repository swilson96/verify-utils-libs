package uk.gov.ida.jerseyclient;

import com.google.inject.Inject;
import org.glassfish.jersey.client.ClientResponse;
import uk.gov.ida.common.ExceptionType;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.ida.exceptions.ApplicationException.createUnauditedException;

public class ErrorHandlingClient {

    private final Client jerseyClient;

    @Inject
    public ErrorHandlingClient(Client jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public Response get(final URI uri) {
        return get(uri, Collections.<Cookie>emptyList(), Collections.<String, String>emptyMap());
    }

    public Response get(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
        try {
            Invocation.Builder requestBuilder = jerseyClient.target(uri).request();
            for (Cookie cookie : cookies) {
                requestBuilder = requestBuilder.cookie(cookie);
            }
            for (Map.Entry<String, String> headerDetail : headers.entrySet()) {
                if (headerDetail.getValue() != null) {
                    requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                }
            }
            return requestBuilder
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .get(Response.class);
        } catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

    public Response post(final URI uri, final Object postBody) {
        return post(uri, Collections.<String, String>emptyMap(), postBody);
    }

    public Response post(final URI uri, final Map<String, String> headers, final Object postBody) {
        try {
            Invocation.Builder requestBuilder = jerseyClient.target(uri).request(MediaType.APPLICATION_JSON_TYPE);
            for(Map.Entry<String, String> headerDetail: headers.entrySet()){
                if(headerDetail.getValue() != null) {
                    requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                }
            }
            return requestBuilder.post(Entity.json(postBody), Response.class);
        } catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

}
