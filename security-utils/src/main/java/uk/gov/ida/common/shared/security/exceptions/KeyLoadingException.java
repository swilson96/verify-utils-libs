package uk.gov.ida.common.shared.security.exceptions;

import static java.text.MessageFormat.format;

public class KeyLoadingException extends RuntimeException {
    public KeyLoadingException(String keyUri, Exception e) {
        super(format("Key file {0} could not be loaded.", keyUri), e);
    }
}
