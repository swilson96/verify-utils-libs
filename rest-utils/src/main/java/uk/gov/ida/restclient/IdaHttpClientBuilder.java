package uk.gov.ida.restclient;

import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.setup.Environment;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.params.BasicHttpParams;

public class IdaHttpClientBuilder extends HttpClientBuilder {

    private final boolean enableStaleConnectionCheck;

    public IdaHttpClientBuilder(Environment environment, boolean enableStaleConnectionCheck) {
        super(environment);
        this.enableStaleConnectionCheck = enableStaleConnectionCheck;
    }

    /**
     * Map the parameters in HttpClientConfiguration to a BasicHttpParams object
     *
     * @return a BasicHttpParams object from the HttpClientConfiguration
     */
    protected BasicHttpParams createHttpParams(String name) {
        final BasicHttpParams params = super.createHttpParams(name);
        params.setParameter(AllClientPNames.STALE_CONNECTION_CHECK, enableStaleConnectionCheck);
        return params;
    }


}
