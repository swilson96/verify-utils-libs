package uk.gov.ida.jerseyclient;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import uk.gov.ida.common.ExceptionType;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Optional.absent;
import static uk.gov.ida.exceptions.ApplicationException.createUnauditedException;

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

    public <T> Map.Entry<T, List<NewCookie>> postAndReturnCookie(Object postBody, URI uri, Class<T> clazz) {
        return responseProcessor.getJsonEntityWithCookie(uri, clazz, executePost(postBody, uri));
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
        return errorHandledClientResponse(new Function<Optional<Object>, ClientResponse>() {
            @Override
            public ClientResponse apply(Optional<Object> input) {
                return jerseyClient.resource(uri)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .get(ClientResponse.class);
            }
        }, uri);
    }

    private ClientResponse executeGet(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
        return errorHandledClientResponse(new Function<Optional<Object>, ClientResponse>() {
            @Override
            public ClientResponse apply(Optional<Object> input) {
                WebResource.Builder requestBuilder = jerseyClient.resource(uri).getRequestBuilder();
                for(Cookie cookie: cookies){
                    requestBuilder = requestBuilder.cookie(cookie);
                }
                for(Map.Entry<String, String> headerDetail: headers.entrySet()){
                    if(headerDetail.getValue() != null) {
                        requestBuilder = requestBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                    }
                }
                return requestBuilder
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .get(ClientResponse.class);
            }
        }, uri);
    }

    private ClientResponse executePost(final Object postBody, final URI uri) {
        return errorHandledClientResponse(new Function<Optional<Object>, ClientResponse>() {
            @Override
            public ClientResponse apply(Optional<Object> input) {
                return jerseyClient.resource(uri).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, postBody);
            }
        }, uri);
    }

    private ClientResponse errorHandledClientResponse(Function<Optional<Object>, ClientResponse> request, URI uri) {
        try {
            return request.apply(absent());
        } catch (ClientHandlerException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }
}
