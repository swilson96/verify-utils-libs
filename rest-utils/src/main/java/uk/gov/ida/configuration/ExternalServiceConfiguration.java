package uk.gov.ida.configuration;

import com.google.common.base.Throwables;
import org.apache.http.client.utils.URIBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class ExternalServiceConfiguration {

    protected ExternalServiceConfiguration() {
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

    public String getHost() {
        return host;
    }

    public String getScheme() {
        return scheme;
    }

    public Integer getPort() {
        return port;
    }

    public URI toUri() {
        try {
            return new URIBuilder().setScheme(getScheme()).setHost(getHost()).setPort(getPort()).build();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    public UriBuilder toUriBuilder() {
        return UriBuilder.fromUri("").scheme(getScheme()).host(getHost()).port(getPort());
    }
}
