package uk.gov.ida.analytics;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.configuration.AnalyticsConfiguration;
import uk.gov.ida.configuration.AnalyticsConfigurationBuilder;

import javax.ws.rs.core.Cookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static uk.gov.ida.analytics.AnalyticsReporter.PIWIK_VISITOR_ID;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsReporterTest {

    @Mock
    private Client client;

    @Mock
    private HttpRequestContext requestContext;

    @Mock
    private HttpContext context;

    @Mock
    private AsyncWebResource asyncWebResource;

    @Mock
    private PiwikClient piwikClient;
    private String visitorId = "123";

    @Before
    public void setUp() throws Exception {
        doReturn(requestContext).when(context).getRequest();
        doReturn(ImmutableMap.of(PIWIK_VISITOR_ID, new Cookie(PIWIK_VISITOR_ID, visitorId))).when(requestContext).getCookies();
        when(requestContext.getRequestUri()).thenReturn(URI.create("http://localhost"));
    }

    @Test
    public void shouldCallGenerateUrlAndSendToPiwikAsynchronouslyWhenReportingCustomVariable() throws Exception {
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build());

        analyticsReporter.reportCustomVariable(2, "IDP", "Experian", context);

        URI expected = analyticsReporter.generateCustomVariableURI(2, "IDP", "Experian", Optional.<String>of(visitorId), context.getRequest());
        verify(piwikClient).report(expected, context.getRequest());
    }

    @Test
    public void shouldHandleAnyExceptionsWhenReportingCustomVariable() throws Exception {
        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        doThrow(new RuntimeException("error")).when(analyticsReporter).generateCustomVariableURI(2, "IDP", "Experian", Optional.<String>of(visitorId), requestContext);

        analyticsReporter.reportCustomVariable(2, "IDP", "Experian", context);
    }

    @Test
    public void shouldCallGenerateUrlAndSendToPiwkAsynchronously() throws MalformedURLException, URISyntaxException {
        String friendlyDescription = "friendly description of URL";
        URI piwikUri = mock(URI.class);

        when(requestContext.getHeaderValue("User-Agent")).thenReturn("Chrome");

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        String requestId = "foo";
        doReturn(requestId).when(analyticsReporter).getRequestId();
        doReturn(piwikUri).when(analyticsReporter).generateURI(friendlyDescription, requestContext, visitorId, requestId);

        analyticsReporter.report(friendlyDescription, context);

        verify(piwikClient).report(piwikUri, requestContext);
    }

    @Test
    public void shouldHandleAnyExceptions() throws MalformedURLException, URISyntaxException {

        String friendlyDescription = "friendly description of URL";

        AnalyticsReporter analyticsReporter = spy(new AnalyticsReporter(piwikClient, new AnalyticsConfigurationBuilder().build()));

        String requestId = "4";
        doThrow(new RuntimeException("error")).when(analyticsReporter).generateURI(friendlyDescription, requestContext, visitorId, requestId);

        analyticsReporter.report(friendlyDescription, context);
    }

    @Test
    public void shouldGeneratePiwikUrl() throws MalformedURLException, URISyntaxException {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        DateTime now = DateTime.now();

        when(requestContext.getHeaderValue("Referer")).thenReturn("http://piwikserver/referrerUrl");
        when(requestContext.getRequestUri()).thenReturn(new URI("http://piwikserver/requestUrl"));

        URIBuilder expectedURI = new URIBuilder("http://piwik-digds.rhcloud.com/analytics?idsite=9595&rec=1&apiv=1&url=http%3A%2F%2Fpiwikserver%2FrequestUrl&urlref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&_id=abc&ref=http%3A%2F%2Fpiwikserver%2FreferrerUrl&cookie=false&r=613892&action_name=SERVER+friendly+description+of+URL");
        expectedURI.addParameter("h", Integer.toString(now.getHourOfDay()));
        expectedURI.addParameter("m", Integer.toString(now.getMinuteOfHour()));
        expectedURI.addParameter("s", Integer.toString(now.getSecondOfMinute()));
        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);

        URIBuilder testURI = new URIBuilder(analyticsReporter.generateURI("SERVER friendly description of URL", requestContext, "abc", "613892"));

        Map<String,NameValuePair> expectedParams = Maps.uniqueIndex(expectedURI.getQueryParams(), new Function<NameValuePair, String>() {
            public String apply(NameValuePair from) {
                return from.getName();
            }
        });

        for(NameValuePair param : testURI.getQueryParams()){
            assertThat(expectedParams).containsEntry(param.getName(), param);
        }

        assertThat(testURI.getQueryParams().size()).isEqualTo(expectedParams.size());

        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldGeneratePiwikFraudUrl() throws MalformedURLException, URISyntaxException {
        URIBuilder expectedURI = new URIBuilder("http://piwiki-dgds.rhcloud.com/analytics?_id=123&idsite=9595&rec=1&apiv=1&e_c=fraud_response&e_a=fraud_response");
        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);
        Optional<Cookie> piwikCookie = fromNullable(requestContext.getCookies().get(PIWIK_VISITOR_ID));
        Optional<String> visitorId = Optional.of(piwikCookie.get().getValue());
        URIBuilder testURI = new URIBuilder(analyticsReporter.generateFraudURI(visitorId));
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

    @Test
    public void shouldGeneratePiwikCustomVariableUrl() throws URISyntaxException {
        String customVariable = "{\"1\":[\"RP\",\"HMRC BLA\"]}";

        URIBuilder expectedURI = new URIBuilder("http://piwiki-dgds.rhcloud.com/analytics?_id=123&idsite=9595&rec=1&apiv=1");
        expectedURI.addParameter("_cvar", customVariable);
        expectedURI.addParameter("url", requestContext.getRequestUri().toString());
        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfigurationBuilder().build();
        AnalyticsReporter analyticsReporter = new AnalyticsReporter(piwikClient, analyticsConfiguration);
        Optional<Cookie> piwikCookie = fromNullable(requestContext.getCookies().get(PIWIK_VISITOR_ID));
        Optional<String> visitorId = Optional.of(piwikCookie.get().getValue());
        URIBuilder testURI = new URIBuilder(analyticsReporter.generateCustomVariableURI(1, "RP", "HMRC BLA", visitorId, context.getRequest()));

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
