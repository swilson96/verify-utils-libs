package uk.gov.ida.jerseyclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.exceptions.ApplicationException;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlingClientTest {

    @Mock
    private Client client;

    @Mock
    private WebResource webResource;

    @Mock
    private WebResource.Builder webResourceBuilder;

    private ErrorHandlingClient errorHandlingClient;
    private URI testUri;

    @Before
    public void setup() {
        errorHandlingClient = new ErrorHandlingClient(client);
        when(client.resource(any(URI.class))).thenReturn(webResource);
        when(webResource.getRequestBuilder()).thenReturn(webResourceBuilder);
        when(webResourceBuilder.accept(Matchers.<MediaType>any())).thenReturn(webResourceBuilder);
        when(webResourceBuilder.type(Matchers.<MediaType>any())).thenReturn(webResourceBuilder);
        when(webResourceBuilder.cookie(Matchers.<Cookie>any())).thenReturn(webResourceBuilder);
        when(webResourceBuilder.header(anyString(), Matchers.any())).thenReturn(webResourceBuilder);

        testUri = URI.create("/some-uri");
    }

    @Test
    public void getWithCookiesAndHeaders_shouldAddCookiesAndHeadersToRequest() throws Exception {
        final Cookie cookie = new Cookie("cookie", "monster");
        final List<Cookie> cookies = ImmutableList.of(cookie);
        final String headerName = "X-Clacks-Overhead";
        final String headerValue = "GNU Terry Pratchett";
        final Map<String, String> headers = ImmutableMap.of(headerName, headerValue);

        errorHandlingClient.get(testUri, cookies, headers);

        verify(webResourceBuilder, times(1)).cookie(cookie);
        verify(webResourceBuilder, times(1)).header(headerName, headerValue);
        verify(webResourceBuilder, times(1)).get(ClientResponse.class);
    }

    @Test(expected = ApplicationException.class)
    public void get_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(client.resource(testUri)).thenThrow(new ClientHandlerException());

        errorHandlingClient.get(testUri);
    }

    @Test
    public void postWithHeaders_shouldAddHeadersToRequest() throws Exception {
        final String headerName = "X-Clacks-Overhead";
        final String headerValue = "GNU Terry Pratchett";
        final Map<String, String> headers = ImmutableMap.of(headerName, headerValue);

        final String postBody = "";
        errorHandlingClient.post(testUri, headers, postBody);

        verify(webResourceBuilder, times(1)).header(headerName, headerValue);
        verify(webResourceBuilder, times(1)).post(ClientResponse.class, postBody);
    }

    @Test(expected = ApplicationException.class)
    public void post_shouldThrowApplicationExceptionWhenAWireProblemOccurs() throws Exception {
        when(client.resource(testUri)).thenThrow(new ClientHandlerException());

        errorHandlingClient.post(testUri, "");
    }

}