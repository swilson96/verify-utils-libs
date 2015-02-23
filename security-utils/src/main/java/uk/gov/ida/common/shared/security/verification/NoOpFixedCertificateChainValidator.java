package uk.gov.ida.common.shared.security.verification;

import java.security.cert.X509Certificate;

public class NoOpFixedCertificateChainValidator extends FixedCertificateChainValidator {

    public NoOpFixedCertificateChainValidator() {
        super(null, null);
    }

    @Override
    public void validate(X509Certificate certificate) {
    }
}