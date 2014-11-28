package uk.gov.ida.restclient;

import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Environment;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.params.BasicHttpParams;

import java.util.HashMap;
import java.util.Map;

public class IdaHttpClientBuilder extends HttpClientBuilder {

    private final Map<String, Object> parameters = new HashMap<>();
    private final boolean enableStaleConnectionCheck;

    public IdaHttpClientBuilder(Environment environment, boolean enableStaleConnectionCheck) {
        super(environment);
        this.enableStaleConnectionCheck = enableStaleConnectionCheck;
    }

    public IdaHttpClientBuilder withParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }

    /**
     * Map the parameters in HttpClientConfiguration to a BasicHttpParams object
     *
     * @return a BasicHttpParams object from the HttpClientConfiguration
     */
    protected BasicHttpParams createHttpParams(String name) {
        final BasicHttpParams params = super.createHttpParams(name);
        params.setParameter(AllClientPNames.STALE_CONNECTION_CHECK, enableStaleConnectionCheck);

        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            params.setParameter(parameter.getKey(), parameter.getValue());
        }
        return params;
    }
}
