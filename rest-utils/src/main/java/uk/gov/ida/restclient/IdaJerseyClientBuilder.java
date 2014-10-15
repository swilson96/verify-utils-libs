package uk.gov.ida.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import javax.validation.Validation;
import javax.validation.Validator;
import java.net.ProxySelector;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class IdaJerseyClientBuilder {
    private final HttpClientBuilder builder;
    private final Map<String, Object> properties = Maps.newLinkedHashMap();

    private JerseyClientConfiguration configuration = new JerseyClientConfiguration();
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private Environment environment;

    public IdaJerseyClientBuilder(Environment environment, boolean enableStaleConnectionCheck) {
        this.environment = environment;
        this.builder = new IdaHttpClientBuilder(environment, enableStaleConnectionCheck);
    }

    /**
     * Sets the state of the given Jersey property.
     *
     * @param propertyName  the name of the Jersey property
     * @param propertyValue the state of the Jersey property
     * @return {@code this}
     */
    public IdaJerseyClientBuilder withProperty(String propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
        return this;
    }

    /**
     * Uses the given {@link JerseyClientConfiguration}.
     *
     * @param configuration a configuration object
     * @return {@code this}
     */
    public IdaJerseyClientBuilder using(JerseyClientConfiguration configuration) {
        this.configuration = configuration;
        builder.using(configuration);
        return this;
    }

    /**
     * Use the given {@link org.apache.http.conn.scheme.SchemeRegistry} instance.
     *
     * @param registry a {@link org.apache.http.conn.scheme.SchemeRegistry} instance
     * @return {@code this}
     */
    public IdaJerseyClientBuilder using(SchemeRegistry registry) {
        builder.using(registry);
        return this;
    }

    /**
     * Uses the {@link HttpRequestRetryHandler} for handling request retries.
     *
     * @param httpRequestRetryHandler an httpRequestRetryHandler
     * @return {@code this}
     */
    public IdaJerseyClientBuilder using(HttpRequestRetryHandler httpRequestRetryHandler) {
        builder.using(httpRequestRetryHandler);
        return this;
    }


    /**
     * Builds the {@link com.sun.jersey.api.client.Client} instance.
     *
     * @return a fully-configured {@link com.sun.jersey.api.client.Client}
     */
    public Client build(String name) {
        return build(environment.lifecycle()
                .executorService("jersey-client-" + name + "-%d")
                .minThreads(configuration.getMinThreads())
                .maxThreads(configuration.getMaxThreads())
                .build(),
                environment.getObjectMapper(),
                name);
    }

    private Client build(ExecutorService threadPool,
                         ObjectMapper objectMapper,
                         String name) {
        final Client client = new ApacheHttpClient4(buildHandler(name), buildConfig(objectMapper));
        client.setExecutorService(threadPool);

        if (configuration.isGzipEnabled()) {
            client.addFilter(new GZIPContentEncodingFilter(configuration.isGzipEnabledForRequests()));
        }

        return client;
    }

    private ApacheHttpClient4Handler buildHandler(String name) {
        // DropWizard's HttpClientBuilder doesn't expose the ability to
        // configure HttpRoutePlanner instances so we have to hack it in
        DefaultHttpClient client = (DefaultHttpClient) builder.build(name);

        client.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()));

        return new ApacheHttpClient4Handler(client, null, true);
    }

    private ApacheHttpClient4Config buildConfig(ObjectMapper objectMapper) {
        final ApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
        config.getSingletons().add(new JacksonMessageBodyProvider(objectMapper, validator));
        config.getProperties().putAll(properties);
        return config;
    }
}
