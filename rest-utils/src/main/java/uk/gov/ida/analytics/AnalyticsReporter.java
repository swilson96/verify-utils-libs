package uk.gov.ida.analytics;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.AnalyticsConfiguration;

import javax.ws.rs.core.Cookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import static com.google.common.base.Optional.fromNullable;

public class AnalyticsReporter {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsReporter.class);
    public static final String PIWIK_VISITOR_ID = "PIWIK_VISITOR_ID";
    public static final String REFERER = "Referer";

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
        uriBuilder.addParameter("url", request.getRequestUri().toString());

        // Only FireFox on Windows is unable to provide referrer on AJAX calls
        Optional<String> refererHeader = fromNullable(request.getHeaderValue(REFERER));
        if(refererHeader.isPresent()) {
            uriBuilder.addParameter("urlref", refererHeader.get());
            uriBuilder.addParameter("ref", refererHeader.get());
        }
        uriBuilder.addParameter("cookie", "false");

        return uriBuilder.build();
    }

    public URI generateCustomVariableURI(int index, String name, String value, Optional<String> visitorId) throws URISyntaxException {
        String customVariable = "{\"" + index +"\":[\""+ name + "\",\""+ value +"\"]}";
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());
        if(visitorId.isPresent()) {
            uriBuilder.addParameter("_id", visitorId.get());
        }
        uriBuilder.addParameter("idsite", analyticsConfiguration.getSiteId().toString());
        uriBuilder.addParameter("apiv", "1");
        uriBuilder.addParameter("rec", "1");
        uriBuilder.addParameter("_cvar",customVariable);

        return uriBuilder.build();
    }

    public URI generateFraudURI(Optional<String> visitorId) throws MalformedURLException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());

        if(visitorId.isPresent()) {
            uriBuilder.addParameter("_id", visitorId.get());
        }
        uriBuilder.addParameter("idsite", analyticsConfiguration.getSiteId().toString());
        uriBuilder.addParameter("apiv", "1");
        uriBuilder.addParameter("rec", "1");
        uriBuilder.addParameter("e_c", "fraud_response");// event category
        uriBuilder.addParameter("e_a", "fraud_response");// event action

        return uriBuilder.build();
    }

    public void reportCustomVariable(int index, String name, String value, HttpContext context) {
        try {
            if (analyticsConfiguration.getEnabled()) {
                HttpRequestContext request = context.getRequest();
                Optional<String> visitorId = fromNullable(request.getCookies().get(PIWIK_VISITOR_ID)).transform(new Function<Cookie, String>() {
                    @Override
                    public String apply(Cookie input) {
                        return input.getValue();
                    }
                });
                piwikClient.reportWithoutContext(generateCustomVariableURI(index, name, value, visitorId));
            }
        } catch (Exception e) {
            LOG.error("Analytics Reporting error", e);
        }
    }

    public void report(String friendlyDescription, HttpContext context) {
        try {
            if (analyticsConfiguration.getEnabled()) {
                HttpRequestContext request = context.getRequest();
                Optional<Cookie> piwikCookie = fromNullable(request.getCookies().get(PIWIK_VISITOR_ID));
                if(piwikCookie.isPresent()) {
                    String visitorId = piwikCookie.get().getValue();
                    piwikClient.report(generateURI(friendlyDescription, request, visitorId, getRequestId()), request);
                }
            }
        } catch (Exception e) {
            LOG.error("Analytics Reporting error", e);
        }
    }

    public void reportFraud(HttpContext context) {
        try {
            if (analyticsConfiguration.getEnabled()) {
                HttpRequestContext request = context.getRequest();
                Optional<Cookie> piwikCookie = fromNullable(request.getCookies().get(PIWIK_VISITOR_ID));
                Optional<String> visitorId = Optional.of(piwikCookie.get().getValue());
                piwikClient.reportWithoutContext(generateFraudURI(visitorId));
            }
        } catch(Exception e) {
            LOG.error("Analytics Reporting error", e);
        }
    }

    protected String getRequestId() {
        return String.valueOf(new Random().nextInt(10000000));
    }

}
