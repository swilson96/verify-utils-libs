package uk.gov.ida.configuration;

import com.google.common.base.Throwables;
import org.apache.http.client.utils.URIBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class DependentServiceConfiguration implements ServiceConfiguration {

    protected DependentServiceConfiguration() {
    }

    DependentServiceConfiguration(String host, String scheme, int port) {
        this.host = host;
        this.scheme = scheme;
        this.port = port;
    }

    @NotNull
    @Valid
    protected String host;

    @NotNull
    @Valid
    protected String scheme;

    @NotNull
    @Valid
    protected Integer port;

    // still allowed for backwards compatibility
    @Valid
    protected String serviceName;

    public String getHost() {
        return host;
    }

    public String getScheme() {
        return scheme;
    }

    public Integer getPort() {
        return port;
    }

    // still allowed for backwards compatibility
    public String getServiceName() {
        return serviceName;
    }

    public URI toBaseUri() {
        try {
            return new URIBuilder().setScheme(getScheme()).setHost(getHost()).setPort(getPort()).build();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public URI getUri() {
        return toBaseUri();
    }
}
