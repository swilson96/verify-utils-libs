package uk.gov.ida.jerseyclient;

import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.util.Duration;

public class JerseyClientConfigurationBuilder {

    public static JerseyClientConfigurationBuilder aJerseyClientConfiguration() {
        return new JerseyClientConfigurationBuilder();
    }

    public JerseyClientConfiguration build() {
        return new TestJerseyClientConfiguration(
                1,
                128,
                Duration.microseconds(500),
                Duration.microseconds(500),
                Duration.hours(1),
                1024,
                1024);
    }

    private static class TestJerseyClientConfiguration extends JerseyClientConfiguration {
        private TestJerseyClientConfiguration(
                int minThreads,
                int maxThreads,

                Duration timeout,
                Duration connectionTimeout,
                Duration timeToLive,
                int maxConnections,
                int maxConnectionsPerRoute) {

            setMinThreads(minThreads);
            setMaxThreads(maxThreads);

            setTimeout(timeout);
            setConnectionTimeout(connectionTimeout);
            setTimeToLive(timeToLive);
            setMaxConnections(maxConnections);
            setMaxConnectionsPerRoute(maxConnectionsPerRoute);
        }
    }
}
