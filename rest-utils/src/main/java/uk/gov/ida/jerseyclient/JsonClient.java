package uk.gov.ida.jerseyclient;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class JsonClient {

    private final Client jerseyClient;
    private final JsonResponseProcessor responseProcessor;

    @Inject
    public JsonClient(Client jerseyClient, JsonResponseProcessor responseProcessor) {
        this.jerseyClient = jerseyClient;
        this.responseProcessor = responseProcessor;
    }

    public <T> T post(Object postBody, URI uri, Class<T> clazz) {
        return responseProcessor.getJsonEntity(uri, null, clazz, executePost(postBody, uri));
    }

    public <T> T post(Object postBody, URI uri, Class<T> clazz, Map<String, String> headers) {
        return responseProcessor.getJsonEntity(uri, null, clazz, executePost(postBody, uri, headers));
    }

    public void post(Object postBody, URI uri) {
        responseProcessor.getJsonEntity(uri, null, null, executePost(postBody, uri));
    }

    public <T> T get(URI uri, Class<T> clazz) {
        return responseProcessor.getJsonEntity(uri, null, clazz, executeGet(uri));
    }

    public <T> T get(URI uri, Class<T> clazz, List<Cookie> cookies, Map<String, String> headers) {
        return responseProcessor.getJsonEntity(uri, null, clazz, executeGet(uri, cookies, headers));
    }

    public <T> T get(URI uri, GenericType<T> genericType) {
        return responseProcessor.getJsonEntity(uri, genericType, null, executeGet(uri));
    }

    private ClientResponse executeGet(final URI uri) {
        return jerseyClient.resource(uri)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(ClientResponse.class);
    }

    private ClientResponse executeGet(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
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
    }

    private ClientResponse executePost(final Object postBody, final URI uri) {
        return jerseyClient.resource(uri).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, postBody);
    }

    private ClientResponse executePost(final Object postBody, final URI uri, final Map<String, String> headers) {
        WebResource.Builder requestBuilder = jerseyClient.resource(uri).getRequestBuilder();
        for (Map.Entry<String, String> headerDetail : headers.entrySet()) {
            if (headerDetail.getValue() != null) {
                requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
            }
        }
        return requestBuilder
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, postBody);
    }

}
