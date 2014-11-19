package uk.gov.ida.jerseyclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.core.MediaType;
import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.healthcheck.UniformInterfaceExceptionBuilder.aUniformInterfaceException;

@RunWith(MockitoJUnitRunner.class)
public class JsonClientTest {

    @Mock
    private Client client;
    @Mock
    private WebResource webResource;
    @Mock
    private WebResource.Builder builder;
    @Mock
    private JsonResponseProcessor jsonResponseProcessor;

    private JsonClient jsonClient;
    private URI testUri = URI.create("/some-uri");
    private String responseBody = "some-response-body";

    @Before
    public void setup() {
        when(client.resource(any(String.class))).thenReturn(webResource);
        when(client.resource(any(URI.class))).thenReturn(webResource);
        when(webResource.accept(any(MediaType.class))).thenReturn(builder);
        when(webResource.type(any(MediaType.class))).thenReturn(builder);
        jsonClient = new JsonClient(client, jsonResponseProcessor);
    }

    @Test
    public void post_shouldDelegateToJsonResponseProcessorToCheckForErrors() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(builder.post(ClientResponse.class, responseBody)).thenReturn(clientResponse);

        jsonClient.post(responseBody, testUri);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, null, clientResponse);
    }

    @Test
    public void basicPost_shouldDelegateToProcessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(builder.post(ClientResponse.class, responseBody)).thenReturn(clientResponse);

        jsonClient.post(responseBody, testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void basicGet_shouldDelegateToProcessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(builder.get(ClientResponse.class)).thenReturn(clientResponse);

        jsonClient.get(testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void getWithGenericType_shouldDelegateToprocessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(builder.get(ClientResponse.class)).thenReturn(clientResponse);
        GenericType<String> genericType = new GenericType<String>() {};

        jsonClient.get(testUri, genericType);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, genericType, null, clientResponse);
    }

    //Wire errors covered by logic in getClientResponseWithGet and getClientResponseWithPost

    @Test(expected = ApplicationException.class)
    public void get_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        reset(client);
        when(client.resource(any(URI.class))).thenThrow(new ClientHandlerException());

        jsonClient.get(testUri, String.class);
    }

    @Test(expected = ApplicationException.class)
    public void getWithGenericType_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        reset(client);
        when(client.resource(any(URI.class))).thenThrow(new ClientHandlerException());

        jsonClient.get(testUri, new GenericType<String>(){});
    }

    @Test(expected = ApplicationException.class)
    public void post_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        reset(client);
        when(client.resource(any(URI.class))).thenThrow(new ClientHandlerException());

        jsonClient.post("", testUri, String.class);
    }

    @Test(expected = ApplicationException.class)
    public void postExpectingNoReturn_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        reset(client);
        when(client.resource(any(URI.class))).thenThrow(new ClientHandlerException());

        jsonClient.post("", testUri);
    }

    private ClientResponse createMockClient204NoContentResponse(){
        ClientResponse clientResponse = mock(ClientResponse.class);
        when(clientResponse.getEntity(Matchers.<Class<?>>any())).thenThrow(aUniformInterfaceException().withStatus(204).build());
        when(clientResponse.getEntity(any(GenericType.class))).thenThrow(aUniformInterfaceException().withStatus(204).build());
        when(clientResponse.hasEntity()).thenReturn(false);
        when(clientResponse.getStatus()).thenReturn(204);
        return clientResponse;
    }
}
