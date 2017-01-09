package uk.gov.ida.jerseyclient;

import org.junit.Test;
import javax.ws.rs.ProcessingException;

import static org.assertj.core.api.Assertions.assertThat;


public class RetryCommandTest {

    @Test
    public void shouldRetryIfFirstAttemptFails() {
        RetryCommand<String> retryCommand = new RetryCommand(2);

        String result = retryCommand.execute(() -> {
            if (retryCommand.getRetryCounter() == 0) throw new RuntimeException("Command Failed");
            else return "SUCCESS";
        });

        assertThat(result).isEqualTo("SUCCESS");
        assertThat(retryCommand.getRetryCounter()).isEqualTo(1);
    }

    @Test(expected = ProcessingException.class)
    public void shouldNotRetryIfRetryCountIs0() {
        RetryCommand<String> retryCommand = new RetryCommand(0);

        String result = retryCommand.execute(() -> {
            if (retryCommand.getRetryCounter() == 0) throw new RuntimeException("Command Failed");
            else return "SUCCESS";
        });
    }

    @Test
    public void shouldNotRetryIfFirstRequestSucceeds() {
        RetryCommand<String> retryCommand = new RetryCommand<>(2);

        String result = retryCommand.execute(() -> "SUCCESS");

        assertThat(result).isEqualTo("SUCCESS");
        assertThat(retryCommand.getRetryCounter()).isEqualTo(0);
    }

    @Test(expected = ProcessingException.class)
    public void shouldThrowProcessingExceptionIfMaxRetriesExceeded(){
        int retries =2;
        RetryCommand<String> retryCommand = new RetryCommand<>(retries);

            retryCommand.execute(() -> {
                if (retryCommand.getRetryCounter() <= retries) throw new RuntimeException("Command Failed");
                else return "SUCCESS";
            });
    }
}