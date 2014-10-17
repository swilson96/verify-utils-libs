package uk.gov.ida.common.shared.security.verification;

import com.google.inject.Inject;
import uk.gov.ida.common.shared.security.verification.FixedCertificateChainValidator;

import java.security.cert.X509Certificate;

public class NoOpFixedCertificateChainValidator extends FixedCertificateChainValidator {

    @Inject
    public NoOpFixedCertificateChainValidator() {

        super(null, null);
    }

    public void validate(X509Certificate certificate) {
    }
}