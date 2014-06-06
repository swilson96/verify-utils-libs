package uk.gov.ida.restclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.conn.scheme.SchemeRegistry;

import java.util.Map;

public abstract class IdaClientBuilder {

    public static Client anIdaJerseyClientWithScheme(Environment environment, JerseyClientConfiguration jerseyClientConfiguration, SchemeRegistry schemeRegistry, Map<String, Object> properties, String clientName, boolean enableStaleConnectionCheck){
        IdaJerseyClientBuilder jerseyClientBuilder = aBaseClientWithScheme(
                environment,
                jerseyClientConfiguration,
                schemeRegistry,
                properties,
                enableStaleConnectionCheck
        );
        return jerseyClientBuilder.build(clientName);
    }

    private static IdaJerseyClientBuilder aBaseClientWithScheme(Environment environment, JerseyClientConfiguration jerseyClientConfiguration, SchemeRegistry schemeRegistry, Map<String, Object> properties, boolean enableStaleConnectionCheck) {
        IdaJerseyClientBuilder jerseyClientBuilder = new IdaJerseyClientBuilder(environment, enableStaleConnectionCheck);
        jerseyClientBuilder
                .using(jerseyClientConfiguration)
                .using(environment)
                .using(schemeRegistry);
        for (Map.Entry<String, Object> props : properties.entrySet()) {
            jerseyClientBuilder.withProperty(props.getKey(), props.getValue());
        }
        jerseyClientBuilder.withProperty(ApacheHttpClient4Config.PROPERTY_ENABLE_BUFFERING, true);
        return jerseyClientBuilder;
    }

}
