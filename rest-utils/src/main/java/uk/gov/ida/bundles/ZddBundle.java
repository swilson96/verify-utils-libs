package uk.gov.ida.bundles;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.configuration.ServiceStatus;
import uk.gov.ida.resources.ServiceStatusResource;
import uk.gov.ida.tasks.SetServiceUnavailableTask;

public class ZddBundle implements Bundle {
    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(Environment environment) {
        environment.jersey().register(new ServiceStatusResource());
        environment.admin().addTask(new SetServiceUnavailableTask(ServiceStatus.getInstance()));
    }
}
