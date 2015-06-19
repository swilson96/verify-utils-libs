package uk.gov.ida.jerseyclient;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import uk.gov.ida.common.ExceptionType;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    private Response executeGet(final URI uri) {
        return errorHandledClientResponse(new Function<Optional<Object>, Response>() {
            @Override
            public Response apply(Optional<Object> input) {
                return getInvocationBuilder(uri)
                        .get();
            }
        }, uri);
    }

    private Invocation.Builder getInvocationBuilder(URI uri) {
        return jerseyClient.target(uri)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE);
    }

    private Response executeGet(final URI uri, final List<Cookie> cookies, final Map<String, String> headers) {
        return errorHandledClientResponse(new Function<Optional<Object>, Response>() {
            @Override
            public Response apply(Optional<Object> input) {
                Invocation.Builder invocationBuilder = getInvocationBuilder(uri);
                for(Cookie cookie: cookies){
                    invocationBuilder = invocationBuilder.cookie(cookie);
                }
                for(Map.Entry<String, String> headerDetail: headers.entrySet()){
                    if(headerDetail.getValue() != null) {
                        invocationBuilder = invocationBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                    }
                }
                return invocationBuilder.get();
            }
        }, uri);
    }

    private Response executePost(final Object postBody, final URI uri) {
        return errorHandledClientResponse(new Function<Optional<Object>, Response>() {
            @Override
            public Response apply(Optional<Object> input) {
                return getInvocationBuilder(uri).post(Entity.json(postBody));
            }
        }, uri);
    }

    private Response executePost(final Object postBody, final URI uri, final Map<String, String> headers) {
        return errorHandledClientResponse(new Function<Optional<Object>, Response>() {
            @Override
            public Response apply(Optional<Object> input) {
                Invocation.Builder invocationBuilder = getInvocationBuilder(uri);
                for(Map.Entry<String, String> headerDetail: headers.entrySet()){
                    if(headerDetail.getValue() != null) {
                        invocationBuilder = invocationBuilder.header(headerDetail.getKey(), headerDetail.getValue());
                    }
                }
                return invocationBuilder.post(Entity.json(postBody));
            }
        }, uri);
    }

    private Response errorHandledClientResponse(Function<Optional<Object>, Response> request, URI uri) {
        try {
            return request.apply(absent());
        } catch (ProcessingException e) {
            throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e, uri);
        }
    }
}
