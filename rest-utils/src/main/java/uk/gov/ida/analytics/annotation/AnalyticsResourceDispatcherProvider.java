package uk.gov.ida.analytics.annotation;

import com.google.inject.Inject;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import uk.gov.ida.analytics.Analytics;

import java.lang.reflect.Method;

public class AnalyticsResourceDispatcherProvider implements ResourceMethodDispatchProvider {

    private final ResourceMethodDispatchProvider provider;
    private AnalyticsRequestDispatcherFactory analyticsRequestDispatcherFactory;

    @Inject
    public AnalyticsResourceDispatcherProvider(
            ResourceMethodDispatchProvider provider,
            AnalyticsRequestDispatcherFactory analyticsRequestDispatcherFactory) {
        this.provider = provider;
        this.analyticsRequestDispatcherFactory = analyticsRequestDispatcherFactory;
    }

    @Override
    public RequestDispatcher create(AbstractResourceMethod resource) {
        RequestDispatcher dispatcher = provider.create(resource);

        Method method = resource.getMethod();
        if (method.isAnnotationPresent(Analytics.class)) {
            String friendlyText = method.getAnnotation(Analytics.class).friendlyText();
            dispatcher = analyticsRequestDispatcherFactory.create(dispatcher, friendlyText);
        }

        return dispatcher;
    }
}
