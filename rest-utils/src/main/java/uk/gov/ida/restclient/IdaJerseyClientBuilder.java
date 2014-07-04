package uk.gov.ida.restclient;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.scheme.SchemeRegistry;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;

public class IdaJerseyClientBuilder {
    private final HttpClientBuilder builder;
    private final List<Object> singletons = Lists.newArrayList();
    private final List<Class<?>> providers = Lists.newArrayList();
    private final Map<String, Boolean> features = Maps.newLinkedHashMap();
    private final Map<String, Object> properties = Maps.newLinkedHashMap();

    private JerseyClientConfiguration configuration = new JerseyClientConfiguration();
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private Environment environment;
    private ObjectMapper objectMapper;
    private ExecutorService executorService;

    public IdaJerseyClientBuilder(Environment environment, boolean enableStaleConnectionCheck) {
        this.builder = new IdaHttpClientBuilder(environment, enableStaleConnectionCheck);
        this.environment = environment;
    }

    public IdaJerseyClientBuilder(MetricRegistry metricRegistry) {
        this.builder = new HttpClientBuilder(metricRegistry);
    }

    /**
     * Adds the given object as a Jersey provider.
     *
     * @param provider a Jersey provider
     * @return {@code this}
     */
    public IdaJerseyClientBuilder withProvider(Object provider) {
        singletons.add(checkNotNull(provider));
        return this;
    }

    /**
     * Adds the given class as a Jersey provider. <p/><b>N.B.:</b> This class must either have a
     * no-args constructor or use Jersey's built-in dependency injection.
     *
     * @param klass a Jersey provider class
     * @return {@code this}
     */
    public IdaJerseyClientBuilder withProvider(Class<?> klass) {
        providers.add(checkNotNull(klass));
        return this;
    }

    /**
     * Sets the state of the given Jersey feature.
     *
     * @param featureName  the name of the Jersey feature
     * @param featureState the state of the Jersey feature
     * @return {@code this}
     */
    @SuppressWarnings("UnusedDeclaration") // basically impossible to test
    public IdaJerseyClientBuilder withFeature(String featureName, boolean featureState) {
        features.put(featureName, featureState);
        return this;
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
     * Uses the given {@link io.dropwizard.setup.Environment}.
     *
     * @param environment a Dropwizard {@link io.dropwizard.setup.Environment}
     * @return {@code this}
     * @see #using(java.util.concurrent.ExecutorService, com.fasterxml.jackson.databind.ObjectMapper)
     */
    public IdaJerseyClientBuilder using(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Use the given {@link org.apache.http.conn.DnsResolver} instance.
     *
     * @param resolver a {@link org.apache.http.conn.DnsResolver} instance
     * @return {@code this}
     */
    public IdaJerseyClientBuilder using(DnsResolver resolver) {
        builder.using(resolver);
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
     * Use the given {@link javax.validation.Validator} instance.
     *
     * @param validator a {@link javax.validation.Validator} instance
     * @return {@code this}
     */
    public IdaJerseyClientBuilder using(Validator validator) {
        this.validator = validator;
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
     * Uses the given {@link java.util.concurrent.ExecutorService} and {@link com.fasterxml.jackson.databind.ObjectMapper}.
     *
     * @param executorService a thread pool
     * @param objectMapper    an object mapper
     * @return {@code this}
     * @see #using(io.dropwizard.setup.Environment)
     */
    public IdaJerseyClientBuilder using(ExecutorService executorService, ObjectMapper objectMapper) {
        this.executorService = executorService;
        this.objectMapper = objectMapper;
        return this;
    }



    /**
     * Builds the {@link com.sun.jersey.api.client.Client} instance.
     *
     * @return a fully-configured {@link com.sun.jersey.api.client.Client}
     */
    public Client build(String name) {
        if ((environment == null) && (executorService == null) && (objectMapper == null)) {
            throw new IllegalStateException("Must have either an environment or both " +
                    "an executor service and an object mapper");
        }

        if (environment == null) {
            return build(executorService, objectMapper, validator, name);
        }

        return build(environment.lifecycle()
                .executorService("jersey-client-" + name + "-%d")
                .minThreads(configuration.getMinThreads())
                .maxThreads(configuration.getMaxThreads())
                .build(),
                environment.getObjectMapper(),
                environment.getValidator(),
                name);
    }

    private Client build(ExecutorService threadPool,
                         ObjectMapper objectMapper,
                         Validator validator,
                         String name) {
        final Client client = new ApacheHttpClient4(buildHandler(name), buildConfig(objectMapper));
        client.setExecutorService(threadPool);

        if (configuration.isGzipEnabled()) {
            client.addFilter(new GZIPContentEncodingFilter(configuration.isGzipEnabledForRequests()));
        }

        return client;
    }

    private ApacheHttpClient4Handler buildHandler(String name) {
        return new ApacheHttpClient4Handler(builder.build(name), null, true);
    }

    private ApacheHttpClient4Config buildConfig(ObjectMapper objectMapper) {
        final ApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
        config.getSingletons().addAll(singletons);
        config.getSingletons().add(new JacksonMessageBodyProvider(objectMapper, validator));
        config.getClasses().addAll(providers);
        config.getFeatures().putAll(features);
        config.getProperties().putAll(properties);
        return config;
    }
}
