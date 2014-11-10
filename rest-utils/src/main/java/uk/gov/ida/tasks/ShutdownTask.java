package uk.gov.ida.tasks;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import uk.gov.ida.configuration.ServerStatus;

import java.io.PrintWriter;

public class ShutdownTask extends Task {

    private ServerStatus serverStatus;

    public ShutdownTask(final ServerStatus serverStatus){
        super("shutdown");
        this.serverStatus = serverStatus;
    }

    @Override
    public void execute(final ImmutableMultimap<String, String> parameters, final PrintWriter output) throws Exception {
        serverStatus.setShutdownSequenceTo(true);
    }
}
