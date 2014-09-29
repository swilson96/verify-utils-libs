package uk.gov.ida.common.shared.security.verification;

import com.google.common.base.Optional;

import java.security.cert.CertPathValidatorException;

public class CertificateValidity {
    private final Optional<CertPathValidatorException.Reason> reason;
    private final Optional<String> description;

    public static CertificateValidity valid() {
        return new CertificateValidity(Optional.<CertPathValidatorException.Reason>absent(), Optional.<String>absent());
    }

    public static CertificateValidity invalid(CertPathValidatorException.Reason reason, String description) {
        return new CertificateValidity(Optional.of(reason), Optional.of(description));
    }


    private CertificateValidity(Optional<CertPathValidatorException.Reason> reason, Optional<String> description) {
        this.reason = reason;
        this.description = description;
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

    public Optional<String> getDescription() {
        return description;
    }
}
