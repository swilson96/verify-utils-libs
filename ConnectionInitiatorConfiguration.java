package uk.gov.ida.docchecking.sharedapi.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.logging.AppenderFactory;
import io.dropwizard.logging.ConsoleAppenderFactory;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public abstract class ConnectionInitiatorConfiguration extends BaseApplicationConfiguration {

    protected ConnectionInitiatorConfiguration() {
        ImmutableList<AppenderFactory> appenders = this.getLoggingFactory().getAppenders();
        ConsoleAppenderFactory consoleAppender = (ConsoleAppenderFactory) Iterables.find(appenders, new Predicate<AppenderFactory>() {
            @Override
            public boolean apply(@Nullable AppenderFactory input) {
                return input instanceof ConsoleAppenderFactory;
            }
        });
        consoleAppender.setLogFormat("%-5p [%d{ISO8601,UTC}] (^_^) %c: [REQ: %X{reqid:-na}] %m%n%rEx");
    }

    @Valid
    @JsonProperty
    @NotNull
    @SuppressWarnings("unused")
    private JerseyClientConfiguration httpClient;

    @JsonProperty
    protected boolean acceptSelfSignedCerts = false;

    public boolean acceptsSelfSignedCerts() {
        return acceptSelfSignedCerts;
    }

    public JerseyClientConfiguration getClientConnectionConfig() {
        return httpClient;
    }
}
