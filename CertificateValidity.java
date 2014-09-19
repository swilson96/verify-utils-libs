package uk.gov.ida.shared.rest.config.verification;

import com.google.common.base.Optional;

import java.security.cert.CertPathValidatorException;

public class CertificateValidity {
    private final Optional<CertPathValidatorException.Reason> reason;

    public static CertificateValidity valid() {
        return new CertificateValidity(Optional.<CertPathValidatorException.Reason>absent());
    }

    public static CertificateValidity invalid(CertPathValidatorException.Reason reason) {
        return new CertificateValidity(Optional.of(reason));
    }

    private CertificateValidity(Optional<CertPathValidatorException.Reason> reason) {
        this.reason = reason;
    }

    public boolean isValid() {
        return !isInvalid();
    }

    public boolean isInvalid() {
        return reason.isPresent();
    }

    public Optional<CertPathValidatorException.Reason> getReason() {
        return reason;
    }
}
