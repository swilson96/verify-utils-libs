package uk.gov.ida.configuration;

import ch.qos.logback.classic.Level;

public interface LogstashConfiguration {
    Boolean shouldUseLogstashFormat();

    Level getLogLevel();
}
