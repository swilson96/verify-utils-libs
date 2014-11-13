package uk.gov.ida.tasks;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import uk.gov.ida.configuration.ServiceStatus;

import java.io.PrintWriter;

public class SetServiceUnavailableTask extends Task {

    private ServiceStatus serviceStatus;

    public SetServiceUnavailableTask(final ServiceStatus serviceStatus){
        super("set-service-unavailable");
        this.serviceStatus = serviceStatus;
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> parameters, final PrintWriter output) throws Exception {
        serviceStatus.setServiceStatus(false);
    }
}
