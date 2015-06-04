package uk.gov.ida.jerseyclient;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import uk.gov.ida.common.ExceptionType;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
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

    public ClientResponse get(final URI uri) {
        return get(uri, Collections.<Cookie>emptyList(), Collections.<String, String>emptyMap());
    }

    public ClientResponse get(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
        try {
            WebResource.Builder requestBuilder = jerseyClient.resource(uri).getRequestBuilder();
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
                    .get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

    public ClientResponse post(final URI uri, final Object postBody) {
        return post(uri, Collections.<String, String>emptyMap(), postBody);
    }

    public ClientResponse post(final URI uri, final Map<String, String> headers, final Object postBody) {
        try {
            WebResource.Builder requestBuilder = jerseyClient.resource(uri).getRequestBuilder();
            for(Map.Entry<String, String> headerDetail: headers.entrySet()){
                if(headerDetail.getValue() != null) {
                    requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                }
            }
            return requestBuilder
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, postBody);
        } catch (ClientHandlerException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }

}
