package uk.gov.ida.analytics.annotation;

import com.google.inject.Inject;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import uk.gov.ida.analytics.AnalyticsReporter;

public class AnalyticsRequestDispatcherFactory {

    private AnalyticsReporter analyticsReporter;

    @Inject
    public AnalyticsRequestDispatcherFactory(AnalyticsReporter analyticsReporter) {
        this.analyticsReporter = analyticsReporter;
    }

    public RequestDispatcher create(RequestDispatcher dispatcher, String friendlyText) {
        return new AnalyticsRequestDispatcher(dispatcher, analyticsReporter, friendlyText);
    }
}
