package uk.gov.ida.jerseyclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import java.util.function.Supplier;

public class RetryCommand<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RetryCommand.class);

    private int retryCounter;
    private int maxRetries;

    public RetryCommand(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetryCounter() { return retryCounter; }

    public T execute(Supplier<T> function) {
        try {
            return function.get();
        } catch (Exception e) {
            return retry(function);
        }
    }

    private T retry(Supplier<T> function) throws RuntimeException {
        retryCounter = 0;

        LOG.warn("Command failed, will be retried " + maxRetries + " times.");
        while (retryCounter < maxRetries) {
            try {
                return function.get();
            } catch (Exception e) {
                retryCounter++;
                LOG.warn("Command failed on retry " + retryCounter + " of " + maxRetries, e);

                if (retryCounter >= maxRetries) {
                    LOG.warn("Max retries exceeded.");
                    break;
                }
            }
        }
        throw new ProcessingException("Command failed on all of " + maxRetries + " retries");
    }

}
