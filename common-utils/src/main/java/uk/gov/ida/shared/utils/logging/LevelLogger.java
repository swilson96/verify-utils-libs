package uk.gov.ida.shared.utils.logging;

import org.apache.log4j.Level;
import org.slf4j.Logger;

import java.util.UUID;

public class LevelLogger {

    public void log(Level level, Exception exception, Logger log, UUID errorId){

        if (level == Level.ERROR) {
            log.error(LogFormatter.formatLog(errorId, exception.getMessage()),exception);
        }

        if (level == Level.WARN) {
            log.warn(LogFormatter.formatLog(errorId, exception.getMessage()), exception);
        }

        if (level == Level.INFO) {
            log.info(LogFormatter.formatLog(errorId, exception.getMessage()),exception);
        }

        if (level == Level.DEBUG) {
            log.debug(LogFormatter.formatLog(errorId, exception.getMessage()), exception);
        }
    }
}
