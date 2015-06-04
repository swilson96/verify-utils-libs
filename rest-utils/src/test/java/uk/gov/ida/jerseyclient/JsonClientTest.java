package uk.gov.ida.jerseyclient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.ExceptionType;
import uk.gov.ida.exceptions.ApplicationException;

import java.net.URI;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.healthcheck.UniformInterfaceExceptionBuilder.aUniformInterfaceException;

@RunWith(MockitoJUnitRunner.class)
public class JsonClientTest {

    @Mock
    private ErrorHandlingClient errorHandlingClient;
    @Mock
    private JsonResponseProcessor jsonResponseProcessor;

    private JsonClient jsonClient;
    private URI testUri = URI.create("/some-uri");
    private String responseBody = "some-response-body";

    @Before
    public void setup() {
        jsonClient = new JsonClient(errorHandlingClient, jsonResponseProcessor);
    }

    @Test
    public void post_shouldDelegateToJsonResponseProcessorToCheckForErrors() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(errorHandlingClient.post(testUri, responseBody)).thenReturn(clientResponse);

        jsonClient.post(responseBody, testUri);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, null, clientResponse);
    }

    @Test
    public void basicPost_shouldDelegateToProcessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(errorHandlingClient.post(testUri, responseBody)).thenReturn(clientResponse);

        jsonClient.post(responseBody, testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void basicGet_shouldDelegateToProcessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);

        jsonClient.get(testUri, String.class);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, null, String.class, clientResponse);
    }

    @Test
    public void getWithGenericType_shouldDelegateToprocessor() throws Exception {
        ClientResponse clientResponse = createMockClient204NoContentResponse();
        when(errorHandlingClient.get(testUri)).thenReturn(clientResponse);
        GenericType<String> genericType = new GenericType<String>() {};

        jsonClient.get(testUri, genericType);

        verify(jsonResponseProcessor, times(1)).getJsonEntity(testUri, genericType, null, clientResponse);
    }

    //Wire errors covered by logic in getClientResponseWithGet and getClientResponseWithPost

    @Test(expected = ApplicationException.class)
    public void get_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(errorHandlingClient.get(testUri)).thenThrow(ApplicationException.createAuditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID()));

        jsonClient.get(testUri, String.class);
    }

    @Test(expected = ApplicationException.class)
    public void getWithGenericType_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(errorHandlingClient.get(testUri)).thenThrow(ApplicationException.createAuditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID()));

        jsonClient.get(testUri, new GenericType<String>(){});
    }

    @Test(expected = ApplicationException.class)
    public void post_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        final String postBody = "";
        when(errorHandlingClient.post(testUri, postBody)).thenThrow(ApplicationException.createAuditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID()));

        jsonClient.post(postBody, testUri, String.class);
    }

    @Test(expected = ApplicationException.class)
    public void postExpectingNoReturn_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        final String postBody = "";
        when(errorHandlingClient.post(testUri, postBody)).thenThrow(ApplicationException.createAuditedException(ExceptionType.NETWORK_ERROR, UUID.randomUUID()));

        jsonClient.post(postBody, testUri);
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
