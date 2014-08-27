package uk.gov.ida.analytics.annotation;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.analytics.Analytics;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class AnalyticsResourceDispatcherProviderTest {

    @Mock
    ResourceMethodDispatchProvider resourceMethodDispatchProvider;

    @Mock
    AnalyticsRequestDispatcherFactory mockAnalyticsDispatcherFactory;

    @Analytics(friendlyText = "friendly text")
    public void mockAnnotatedMethod(){}

    @Test
    public void ifAnalyticsAnnotationPresentAnalyticsDispatcherShouldBeReturned() throws Exception {
        RequestDispatcher mockDispatcher = mock(RequestDispatcher.class);
        RequestDispatcher mockAnalyticsDispatcher = mock(RequestDispatcher.class);

        AbstractResourceMethod mockResourceMethod = mock(AbstractResourceMethod.class);
        Method mockAnnotatedMethod = this.getClass().getMethod("mockAnnotatedMethod");

        doReturn(mockDispatcher).when(resourceMethodDispatchProvider).create(mockResourceMethod);
        doReturn(mockAnnotatedMethod).when(mockResourceMethod).getMethod();

        doReturn(mockAnalyticsDispatcher).when(mockAnalyticsDispatcherFactory).create(mockDispatcher, "friendly text");

        AnalyticsResourceDispatcherProvider analyticsResourceDispatcherProvider =
                new AnalyticsResourceDispatcherProvider(resourceMethodDispatchProvider, mockAnalyticsDispatcherFactory);

        RequestDispatcher result = analyticsResourceDispatcherProvider.create(mockResourceMethod);

        assertThat(result).isEqualTo(mockAnalyticsDispatcher);
    }
}