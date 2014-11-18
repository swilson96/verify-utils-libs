package uk.gov.ida.jerseyclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.ErrorStatusDto;
import uk.gov.ida.common.ExceptionType;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.gov.ida.exceptions.ApplicationException.createExceptionFromErrorStatusDto;
import static uk.gov.ida.exceptions.ApplicationException.createUnauditedException;

public class JsonResponseProcessor {

    private final ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(JsonResponseProcessor.class);

    @Inject
    public JsonResponseProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T getJsonEntity(URI uri, GenericType<T> genericType, Class<T> clazz, ClientResponse clientResponse) {
        ClientResponse successResponse = filterErrorResponses(uri, clientResponse);
        try {
            if ((clazz != null && clazz == ClientResponse.class) || (genericType != null && genericType.getRawClass() == ClientResponse.class)) {
                throw createUnauditedException(ExceptionType.INVALID_CLIENTRESPONSE_PARAM, UUID.randomUUID());
            } else if (clazz == null && genericType == null) {
                return (T) new Object();
            }
            return getEntity(genericType, clazz, successResponse);
        } finally {
            clientResponse.close(); //Do this to avoid any possibility of a connection leak.
        }
    }

    public <T> Map.Entry<T, List<NewCookie>> getJsonEntityWithCookie(URI uri, Class<T> clazz, ClientResponse clientResponse) {
        ClientResponse successResponse = filterErrorResponses(uri, clientResponse);
        try {
            T entity = getEntity(null, clazz, successResponse);
            List<NewCookie> cookies = clientResponse.getCookies();
            return new AbstractMap.SimpleImmutableEntry<>(entity, cookies);
        } finally {
            clientResponse.close();
        }
    }

    private <T> T getEntity(GenericType<T> genericType, Class<T> entityClazz, ClientResponse clientResponse) {
        if (clientResponse.hasEntity()) {
            try {
                if (entityClazz != null) {
                    return clientResponse.getEntity(entityClazz);
                } else if (genericType != null) {
                    return clientResponse.getEntity(genericType);
                }
            } catch (ClientHandlerException e) {
                throw createUnauditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID(), e);
            }
        }
        throw new IllegalArgumentException("Client response has no entity.");
    }

    private ClientResponse filterErrorResponses(URI uri, ClientResponse clientResponse) {
        switch (clientResponse.getStatusInfo().getFamily()) {
            case SERVER_ERROR:
                throw createErrorStatus(clientResponse, ExceptionType.REMOTE_SERVER_ERROR, uri);
            case CLIENT_ERROR:
                throw createErrorStatus(clientResponse, ExceptionType.CLIENT_ERROR, uri);
            default:
                return clientResponse;
        }
    }

    private ApplicationException createErrorStatus(ClientResponse clientResponse, ExceptionType exceptionType, URI uri) {
        String entity = clientResponse.getEntity(String.class);
        try {
            return createExceptionFromErrorStatusDto(objectMapper.readValue(entity, ErrorStatusDto.class));
        } catch (IOException e) {
            LOG.error("Unexpected status code [{}] returned from service using URI: {}. Body: {}",
                    clientResponse.getStatus(), uri, entity);
            return createUnauditedException(exceptionType, UUID.randomUUID(), e, uri);
        }
    }
}