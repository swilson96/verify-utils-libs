package uk.gov.ida.configuration.utils;

import ch.qos.logback.classic.Level;

public interface LogstashConfiguration {
    Boolean shouldUseLogstashFormat();

    Level getLogLevel();
}
