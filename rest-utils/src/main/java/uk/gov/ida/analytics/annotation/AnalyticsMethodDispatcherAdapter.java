package uk.gov.ida.analytics.annotation;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import uk.gov.ida.analytics.AnalyticsReporter;

import javax.ws.rs.ext.Provider;

@Provider
public class AnalyticsMethodDispatcherAdapter implements ResourceMethodDispatchAdapter {

    private AnalyticsReporter analyticsReporter;
    private AnalyticsRequestDispatcherFactory analyticsRequestDispatcherFactory;

    @Inject
    public AnalyticsMethodDispatcherAdapter(
            AnalyticsReporter analyticsReporter,
            AnalyticsRequestDispatcherFactory analyticsRequestDispatcherFactory) {
        this.analyticsReporter = analyticsReporter;
        this.analyticsRequestDispatcherFactory = analyticsRequestDispatcherFactory;
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new AnalyticsResourceDispatcherProvider(provider, analyticsRequestDispatcherFactory);
    }
}