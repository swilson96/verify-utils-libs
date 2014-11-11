package uk.gov.ida.shared.utils.logging;

import org.apache.log4j.Level;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LevelLoggerTest {

    private LevelLogger levelLogger = new LevelLogger();

    @Mock
    private Logger logger;

    @Test
    public void assertExceptionIsLoggedWithLevelError() {
        levelLogger.log(Level.ERROR, new RuntimeException("message"), logger, UUID.randomUUID());
        verify(logger).error(any(String.class), any(Exception.class));
    }

    @Test
    public void assertExceptionIsLoggedWithLevelWarn() {
        levelLogger.log(Level.WARN, new RuntimeException("message"), logger, UUID.randomUUID());
        verify(logger).warn(any(String.class), any(Exception.class));
    }

    @Test
    public void assertExceptionIsLoggedWithLevelInfo() {
        levelLogger.log(Level.INFO, new RuntimeException("message"), logger, UUID.randomUUID());
        verify(logger).info(any(String.class), any(Exception.class));
    }

    @Test
    public void assertExceptionIsLoggedWithLevelDebug() {
        levelLogger.log(Level.DEBUG, new RuntimeException("message"), logger, UUID.randomUUID());
        verify(logger).debug(any(String.class), any(Exception.class));
    }
}
