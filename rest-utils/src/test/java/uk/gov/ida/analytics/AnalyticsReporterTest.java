package uk.gov.ida.analytics;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.core.HttpRequestContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.configuration.AnalyticsConfiguration;
import uk.gov.ida.configuration.AnalyticsConfigurationBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsReporterTest {

    @Mock
    private Client client;

    @Mock
    private HttpRequestContext requestContext;

    @Mock
    private AsyncWebResource asyncWebResource;

    @Mock
    private PiwikClient piwikClient;

    @Test
    public void shouldCallGenerateUrlAndSendToPiwkAsynchronously() throws MalformedURLException, URISyntaxException {
        String visitorID = "abc";
        String friendlyDescription = "friendly description of URL";
        URI piwikUri = mock(URI.class);

        when(requestContext.getHeaderValue("User-Agent")).thenReturn("Chrome");

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        String requestId = "foo";
        doReturn(requestId).when(analyticsReporter).getRequestId();
        doReturn(piwikUri).when(analyticsReporter).generateURI(friendlyDescription, requestContext, visitorID, requestId);

        analyticsReporter.report(friendlyDescription, requestContext, visitorID);

        verify(piwikClient).report(piwikUri, requestContext);
    }

    @Test
    public void shouldHandleAnyExceptions() throws MalformedURLException, URISyntaxException {

        String visitorID = "abc";
        String friendlyDescription = "friendly description of URL";

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        String requestId = "4";
        doThrow(new RuntimeException("error")).when(analyticsReporter).generateURI(friendlyDescription, requestContext, visitorID, requestId);

        analyticsReporter.report(friendlyDescription, requestContext, visitorID);
    }

    @Test
    public void shouldGeneratePiwikUrl() throws MalformedURLException, URISyntaxException {
        when(requestContext.getHeaderValue("Referer")).thenReturn("http://piwikserver/referrerUrl");
        when(requestContext.getRequestUri()).thenReturn(new URI("http://piwikserver/requestUrl"));

        URIBuilder expectedURI = new URIBuilder("http://piwik-digds.rhcloud.com/analytics?idsite=9595&rec=1&apiv=1&url=http%3A%2F%2Fpiwikserver%2FreferrerUrl&urlref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&_id=abc&ref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&cookie=false&r=613892&action_name=friendly+description+of+URL");
        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);

        URIBuilder testURI = new URIBuilder(analyticsReporter.generateURI("friendly description of URL", requestContext, "abc", "613892"));

        Map<String,NameValuePair> expectedParams = Maps.uniqueIndex(expectedURI.getQueryParams(), new Function<NameValuePair, String>() {
            public String apply(NameValuePair from) {
                return from.getName();
            }
        });

        for(NameValuePair param : testURI.getQueryParams()){
            assertThat(expectedParams).containsEntry(param.getName(), param);
        }

        assertThat(testURI.getQueryParams().size()).isEqualTo(expectedParams.size());
    }
}
