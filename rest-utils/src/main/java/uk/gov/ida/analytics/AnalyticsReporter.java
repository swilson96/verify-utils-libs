package uk.gov.ida.analytics;

import com.google.inject.Inject;
import com.sun.jersey.api.core.HttpRequestContext;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.AnalyticsConfiguration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class AnalyticsReporter {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsReporter.class);
    public static final String PIWIK_VISITOR_ID = "PIWIK_VISITOR_ID";

    private AnalyticsConfiguration analyticsConfiguration;
    private PiwikClient piwikClient;

    @Inject
    public AnalyticsReporter(PiwikClient piwikClient, AnalyticsConfiguration analyticsConfiguration) {
        this.piwikClient = piwikClient;
        this.analyticsConfiguration = analyticsConfiguration;
    }

    public URI generateURI(String friendlyDescription, HttpRequestContext request, String visitorID, String requestId) throws MalformedURLException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());

        uriBuilder.addParameter("_id", visitorID);
        uriBuilder.addParameter("idsite", analyticsConfiguration.getSiteId().toString());
        uriBuilder.addParameter("action_name", friendlyDescription);
        uriBuilder.addParameter("apiv", "1");
        uriBuilder.addParameter("rec", "1");
        uriBuilder.addParameter("r", requestId);
        uriBuilder.addParameter("url", request.getHeaderValue("Referer"));
        uriBuilder.addParameter("urlref", request.getHeaderValue("Referer"));
        uriBuilder.addParameter("ref", request.getHeaderValue("Referer"));
        uriBuilder.addParameter("cookie", "false");

        return uriBuilder.build();
    }

    public void report(String friendlyDescription, HttpRequestContext request, String visitorID) {
        try {
            piwikClient.report(generateURI(friendlyDescription, request, visitorID, getRequestId()), request);
        } catch (Exception e) {
            LOG.error("Analytics Reporting error", e);
        }
    }

    protected String getRequestId() {
        return String.valueOf(new Random().nextInt(10000000));
    }

    public void report(String friendlyText, HttpRequestContext context) {
        String visitorId = context.getCookies().get(PIWIK_VISITOR_ID).getValue();
        report(friendlyText, context, visitorId);
    }
}
