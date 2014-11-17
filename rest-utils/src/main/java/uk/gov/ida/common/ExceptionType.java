package uk.gov.ida.common;

import org.apache.log4j.Level;

public enum ExceptionType {
    DUPLICATE_SESSION(Level.INFO),
    FORBIDDEN_RESOURCE(Level.INFO),
    CLIENT_ERROR,
    IDA_SOAP,
    IDP_DISABLED(Level.INFO),
    IDP_NOT_SELECTED(Level.INFO),
    INVALID_CLIENTRESPONSE_PARAM,
    INVALID_INPUT(Level.INFO),
    INVALID_SAML(Level.INFO),
    INVALID_SECURE_COOKIE(Level.INFO),
    INVALID_RELAY_STATE(Level.INFO),
    JSON_PARSING,
    MATCHING_SERVICE_REQUEST_ERROR,
    NOT_FOUND(Level.INFO),
    RUNTIME_ERROR,
    SESSION_NOT_FOUND(Level.INFO),
    SESSION_TIMEOUT(Level.INFO),
    STATE_PROCESSING_VALIDATION(Level.INFO),
    TRANSACTION_DISABLED(Level.INFO),
    UNKNOWN,
    INVALID_STATE(Level.INFO),
    INVALID_ASSERTION_CONSUMER_INDEX,
    NETWORK_ERROR,
    REMOTE_SERVER_ERROR,
    UNCHAINED_CERT,
    INVALID_STATE_EXPECTED_RETURN_FROM_IDP_BUT_GOT_SESSION_STARTED(Level.INFO);

    private final Level level;

    ExceptionType() {
        this.level = Level.ERROR;
    }

    ExceptionType(Level Level) {
        this.level = Level;
    }

    public Level getLevel() {
        return level;
    }
}
