package uk.gov.ida.shared.rest.config.verification.exceptions;

public class CertificateChainValidationException extends RuntimeException {
    public CertificateChainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
