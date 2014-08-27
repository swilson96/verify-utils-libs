package uk.gov.ida.analytics.annotation;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import uk.gov.ida.analytics.AnalyticsReporter;

public class AnalyticsRequestDispatcher implements RequestDispatcher {

    private final RequestDispatcher underlying;
    private final AnalyticsReporter analyticsReporter;
    private final String friendlyText;

    public AnalyticsRequestDispatcher(RequestDispatcher underlying, AnalyticsReporter analyticsReporter, String friendlyText) {
        this.underlying = underlying;
        this.analyticsReporter = analyticsReporter;
        this.friendlyText = friendlyText;
    }

    @Override
    public void dispatch(Object resource, HttpContext context) {
        analyticsReporter.report(friendlyText, context.getRequest());
        underlying.dispatch(resource, context);
    }
}
