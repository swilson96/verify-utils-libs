package uk.gov.ida.common;

public enum ExceptionType {
    DUPLICATE_SESSION,
    FORBIDDEN_RESOURCE,
    CLIENT_ERROR,
    IDA_SOAP,
    IDP_DISABLED,
    IDP_NOT_SELECTED,
    INVALID_CLIENTRESPONSE_PARAM,
    INVALID_INPUT,
    INVALID_SAML,
    INVALID_SECURE_COOKIE,
    JSON_PARSING,
    MATCHING_SERVICE_REQUEST_ERROR,
    NOT_FOUND,
    RUNTIME_ERROR,
    SESSION_NOT_FOUND,
    SESSION_TIMEOUT,
    STATE_PROCESSING_VALIDATION,
    TRANSACTION_DISABLED,
    UNKNOWN,
    INVALID_STATE,
    INVALID_ASSERTION_CONSUMER_INDEX,
    NETWORK_ERROR,
    REMOTE_SERVER_ERROR,
    UNCHAINED_CERT,
    INVALID_STATE_EXPECTED_RETURN_FROM_IDP_BUT_GOT_SESSION_STARTED;
}
