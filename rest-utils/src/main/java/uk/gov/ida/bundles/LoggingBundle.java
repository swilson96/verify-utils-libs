package uk.gov.ida.bundles;

import ch.qos.logback.classic.LoggerContext;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.ServiceNameConfiguration;
import uk.gov.ida.dropwizard.logstash.LogstashBundle;

public class LoggingBundle implements ConfiguredBundle<ServiceNameConfiguration> {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(new LogstashBundle());
    }

    @Override
    public void run(ServiceNameConfiguration configuration, Environment environment) throws Exception {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Add service-name to context for easy search in kibana
        context.putProperty("service-name", configuration.getServiceName());
    }
}
