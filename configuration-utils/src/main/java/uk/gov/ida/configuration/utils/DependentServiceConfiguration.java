package uk.gov.ida.configuration.utils;

import com.google.common.base.Throwables;
import org.apache.http.client.utils.URIBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class DependentServiceConfiguration {

    protected DependentServiceConfiguration() {
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

    @NotNull
    @Valid
    protected String serviceName;

    @Valid
    protected String pathPrefix;

    public String getHost() {
        return host;
    }

    public String getScheme() {
        return scheme;
    }

    public Integer getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public URI toBaseUri() {
        try {
            return new URIBuilder().setScheme(getScheme()).setHost(getHost()).setPort(getPort()).build();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }
}
