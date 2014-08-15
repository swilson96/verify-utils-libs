package uk.gov.ida.healthcheck;

import com.google.common.base.Optional;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.header.InBoundHeaders;

import java.io.ByteArrayInputStream;import java.lang.Integer;

public class UniformInterfaceExceptionBuilder {
    private Optional<Integer> status = Optional.absent();

    public static UniformInterfaceExceptionBuilder aUniformInterfaceException() {
        return new UniformInterfaceExceptionBuilder();
    }

    public UniformInterfaceException build() {
        return new UniformInterfaceException(new ClientResponse(status.or(500), new InBoundHeaders(), new ByteArrayInputStream("".getBytes()), null));
    }

    public UniformInterfaceExceptionBuilder withStatus(int status) {
        this.status = Optional.fromNullable(status);
        return this;
    }

}
