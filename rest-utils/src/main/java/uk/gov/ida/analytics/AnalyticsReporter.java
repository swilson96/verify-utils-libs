package uk.gov.ida.analytics;

import com.google.common.base.Optional;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.server.ContainerRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.AnalyticsConfiguration;

import javax.inject.Inject;
import javax.ws.rs.core.Cookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

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

    public void reportCustomVariable(String friendlyDescription, ContainerRequest context, CustomVariable customVariable) {
        reportToPiwik(friendlyDescription, context, Optional.of(customVariable));
    }

    public void report(String friendlyDescription, ContainerRequest context) {
        reportToPiwik(friendlyDescription, context, Optional.<CustomVariable>absent());
    }

    public void reportFraud(ContainerRequest context) {
        if(analyticsConfiguration.getEnabled()) {
            try {
                report(generateFraudURI(getVisitorId(context)), context);
            }
            catch(Exception e) {
                LOG.error("Analytics Reporting error", e);
            }
        }
    }

    public void simulatePageView(String pageTitle, String uri, ContainerRequest context) {
        try {
            report(generateCustomURI(pageTitle, uri, getVisitorId(context)), context);
        }
        catch(Exception e) {
            LOG.error("Analytics Reporting error", e);
        }
    }

    private Optional<String> getVisitorId(ContainerRequest context) {
        return fromNullable(context.getCookies().get(PIWIK_VISITOR_ID)).transform(Cookie::getValue);
    }

    private URI generateCustomURI(String friendlyDescription, String url, Optional<String> visitorId) throws
            URISyntaxException {
        return buildNonFraudURI(friendlyDescription, url, visitorId).build();
    }

    private URIBuilder buildBaseURI(Optional<String> visitorId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());
        if(visitorId.isPresent()) {
            uriBuilder.addParameter("_id", visitorId.get());
        }
        return uriBuilder
                .addParameter("idsite", analyticsConfiguration.getSiteId().toString())
                .addParameter("apiv", "1")
                .addParameter("rec", "1");
    }

    private URIBuilder buildNonFraudURI(String friendlyDescription, String url, Optional<String> visitorId) throws
            URISyntaxException {
        return buildBaseURI(visitorId)
                .addParameter("action_name", friendlyDescription)
                .addParameter("url", url)
                .addParameter("cdt", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(DateTime.now()));
        // TODO         uriBuilder.addParameter("cookie", "false");

    }

    protected URI generateURI(String friendlyDescription, ContainerRequest request, Optional<CustomVariable> customVariable, Optional<String> visitorId) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(analyticsConfiguration.getPiwikServerSideUrl());
        if(visitorId.isPresent()) {
            uriBuilder.addParameter("_id", visitorId.get());
        }
        uriBuilder.addParameter("idsite", analyticsConfiguration.getSiteId().toString());
        uriBuilder.addParameter("action_name", friendlyDescription);
        uriBuilder.addParameter("apiv", "1");
        uriBuilder.addParameter("rec", "1");
        if(customVariable.isPresent()) {
            uriBuilder.addParameter("_cvar", customVariable.get().getJson());
        }
        uriBuilder.addParameter("url", request.getRequestUri().toString());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        uriBuilder.addParameter("cdt", fmt.print(DateTime.now()));

        // Only FireFox on Windows is unable to provide referrer on AJAX calls
        Optional<String> refererHeader = fromNullable(request.getHeaderString(REFERER));
        if(refererHeader.isPresent()) {
            uriBuilder.addParameter("urlref", refererHeader.get());
            uriBuilder.addParameter("ref", refererHeader.get());
        }
        uriBuilder.addParameter("cookie", "false");

        return uriBuilder.build();
    }

    protected URI generateFraudURI(Optional<String> visitorId) throws MalformedURLException, URISyntaxException {
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

    private void report(URI uri, ContainerRequest context) {
        piwikClient.report(uri, context);
    }

    private void reportToPiwik(String friendlyDescription, ContainerRequest context, Optional<CustomVariable> customVariable) {
        if (analyticsConfiguration.getEnabled()) {
            try {
                report(generateURI(friendlyDescription, context, customVariable, getVisitorId(context)), context);
            }
            catch(Exception e) {
                LOG.error("Analytics Reporting error", e);
            }
        }
    }
}
