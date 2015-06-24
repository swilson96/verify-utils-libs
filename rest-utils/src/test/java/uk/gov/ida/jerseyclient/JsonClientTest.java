package uk.gov.ida.jerseyclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.ExceptionType;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JsonClientTest {

    @Mock
    private ErrorHandlingClient errorHandlingClient;
    @Mock
    private Client client;
    @Mock
    private WebTarget webTarget;
    @Mock
    private JsonResponseProcessor jsonResponseProcessor;
    @Mock
    private Invocation.Builder builder;

    private JsonClient jsonClient;
    private URI testUri = URI.create("/some-uri");
    private String requestBody = "some-request-body";

    @Before
    public void setup() {
        when(client.target(any(String.class))).thenReturn(webTarget);
        when(client.target(any(URI.class))).thenReturn(webTarget);
        when(webTarget.request(any(MediaType.class))).thenReturn(builder);
        when(builder.accept(any(MediaType.class))).thenReturn(builder);
        jsonClient = new JsonClient(errorHandlingClient, jsonResponseProcessor);
    }

    @Test
    public void post_shouldDelegateToJsonResponseProcessorToCheckForErrors() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.post(testUri, requestBody)).thenReturn(clientResponse);

        jsonClient.post(requestBody, testUri);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, null, clientResponse);
    }

    @Test
    public void basicPost_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.post(testUri, requestBody)).thenReturn(clientResponse);

        jsonClient.post(requestBody, testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void basicGet_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);

        jsonClient.get(testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void getWithGenericType_shouldDelegateToProcessor() throws Exception {
        Response clientResponse = Response.noContent().build();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);
        GenericType<String> genericType = new GenericType<String>() {};

        jsonClient.get(testUri, genericType);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, genericType, null, clientResponse);
    }
}
