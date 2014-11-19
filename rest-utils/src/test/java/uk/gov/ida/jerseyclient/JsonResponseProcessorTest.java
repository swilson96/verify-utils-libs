package uk.gov.ida.jerseyclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.common.ErrorStatusDto;
import uk.gov.ida.common.ExceptionType;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonResponseProcessorTest {

    private URI uri = URI.create("http://somedomain.com");
    private UUID errorId = UUID.randomUUID();
    private ExceptionType exceptionType = ExceptionType.INVALID_SAML;

    private JsonResponseProcessor responseProcessor;

    @Before
    public void setUp() throws Exception {
        responseProcessor = new JsonResponseProcessor(new ObjectMapper());

    }

    @Test
    public void getJsonEntity_shouldThrowExceptionBasedOnErrorStatusDtoIfOneIsReturned() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(400, ErrorStatusDto.createAuditedErrorStatus(errorId, exceptionType));
        try {
            responseProcessor.getJsonEntity(uri, null, Object.class, clientResponse);
            fail("fail");
        } catch(ApplicationException e) {
            verify(clientResponse, times(1)).getEntity(String.class);
            assertThat(e.getExceptionType()).isEqualTo(exceptionType);
            assertThat(e.getErrorId()).isEqualTo(errorId);
            assertThat(e.isAudited()).isEqualTo(true);
        }
    }

    @Test
    public void getJson_shouldThrowUnauditedErrorExceptionIfClientErrorResponseIsReturned() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(400, "Some Entity");
        try {
            responseProcessor.getJsonEntity(uri, null, Object.class, clientResponse);
            fail("fail");
        } catch(ApplicationException e) {
            verify(clientResponse, times(1)).getEntity(String.class);
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.CLIENT_ERROR);
            assertThat(e.isAudited()).isEqualTo(false);
        }
    }

    @Test
    public void getJson_shouldThrowResourceNotFoundExceptionIf404ResponseIsReturned() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(404, "Some Entity");
        try {
            responseProcessor.getJsonEntity(uri, null, Object.class, clientResponse);
            fail("fail");
        } catch(ApplicationException e) {
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.CLIENT_ERROR);
            assertThat(e.isAudited()).isEqualTo(false);
        }
    }

    @Test
    public void getJson_shouldThrowUnauditedErrorExceptionIfServerErrorResponseIsReturned() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(500, "There has been some internal server error");
        try {
            responseProcessor.getJsonEntity(uri, null, Object.class, clientResponse);
            fail("fail");
        } catch(ApplicationException e) {
            verify(clientResponse, times(1)).getEntity(String.class);
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.REMOTE_SERVER_ERROR);
        }
    }

    @Test
    public void getJson_shouldThrowWhenClientResponseIsRequested() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(200, "some entity");

        try {
            responseProcessor.getJsonEntity(uri, null, ClientResponse.class, clientResponse);
            fail("fail");
        } catch (ApplicationException e) {
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.INVALID_CLIENTRESPONSE_PARAM);
        }
    }

    @Test
    public void getJson_shouldThrowWhenClientResponseGenericTypeIsRequested() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(200, "some entity");

        try {
            responseProcessor.getJsonEntity(uri, new GenericType<ClientResponse>(){}, null, clientResponse);
            fail("fail");
        } catch (ApplicationException e) {
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.INVALID_CLIENTRESPONSE_PARAM);
        }
    }

    @Test
    public void getJsonEntity_shouldCloseClientResponse() throws Exception {
        ClientResponse clientResponse = createMockClientResponse(200, "some entity");

        responseProcessor.getJsonEntity(uri, null, String.class, clientResponse);

        verify(clientResponse).close();
    }

    @Test
    public void getJsonEntity_shouldReturnEmptyStringWhenNoClassNorGenericTypeSupplied() throws Exception {
        responseProcessor.getJsonEntity(uri, null, null, createMockClientResponse(200, "some entity"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getJsonEntity_shouldThrowWhenNoEntityPresent() throws Exception {
        responseProcessor.getJsonEntity(uri, null, String.class, createMock204Response());
    }

    @Test
    public void getJsonEntity_shouldThrowWhenGettingEntityFails() throws Exception {
        try {
            responseProcessor.getJsonEntity(uri, null, String.class, createClientResponseWithBadEntity());
            fail("fail");
        } catch (ApplicationException e) {
            assertThat(e.getExceptionType()).isEqualTo(ExceptionType.NETWORK_ERROR);
            assertThat(e.isAudited()).isEqualTo(false);
        }
    }

    private ClientResponse createClientResponseWithBadEntity() {
        int status = 200;
        ClientResponse clientResponse = mock(ClientResponse.class);
        when(clientResponse.getEntity(any(Class.class))).thenThrow(new ClientHandlerException("argh!"));
        when(clientResponse.hasEntity()).thenReturn(true);
        when(clientResponse.getStatus()).thenReturn(status);
        when(clientResponse.getStatusInfo()).thenReturn(ClientResponse.Status.fromStatusCode(status));
        return clientResponse;
    }

    private ClientResponse createMockClientResponse(int status, Object responseEntity) throws JsonProcessingException {
        ClientResponse clientResponse = mock(ClientResponse.class);
        ObjectMapper objectMapper = new ObjectMapper();
        when(clientResponse.getEntity(String.class)).thenReturn(objectMapper.writeValueAsString(responseEntity));
        when(clientResponse.getEntity(ClientResponse.class)).thenThrow(new RuntimeException("Can't deserialize json to ClientResponse"));
        when(clientResponse.hasEntity()).thenReturn(true);
        when(clientResponse.getStatus()).thenReturn(status);
        when(clientResponse.getStatusInfo()).thenReturn(ClientResponse.Status.fromStatusCode(status));
        return clientResponse;
    }

    private ClientResponse createMockClientResponseWithCookie(int status, Object responseEntity, NewCookie cookie) throws JsonProcessingException {
        ClientResponse clientResponse = mock(ClientResponse.class);
        ObjectMapper objectMapper = new ObjectMapper();
        when(clientResponse.getCookies()).thenReturn(ImmutableList.of(cookie));
        when(clientResponse.getEntity(String.class)).thenReturn(objectMapper.writeValueAsString(responseEntity));
        when(clientResponse.getEntity(ClientResponse.class)).thenThrow(new RuntimeException("Can't deserialize json to ClientResponse"));
        when(clientResponse.hasEntity()).thenReturn(true);
        when(clientResponse.getStatus()).thenReturn(status);
        when(clientResponse.getStatusInfo()).thenReturn(ClientResponse.Status.fromStatusCode(status));
        return clientResponse;
    }

    private ClientResponse createMock204Response() throws JsonProcessingException {
        int status = 204;
        ClientResponse clientResponse = mock(ClientResponse.class);
        when(clientResponse.hasEntity()).thenReturn(false);
        when(clientResponse.getStatus()).thenReturn(status);
        when(clientResponse.getStatusInfo()).thenReturn(ClientResponse.Status.fromStatusCode(status));
        return clientResponse;
    }
}
