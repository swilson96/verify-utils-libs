package uk.gov.ida.common.shared.security.verification;

import java.security.cert.X509Certificate;

public class NoOpFixedCertificateChainValidator extends FixedCertificateChainValidator {

    public NoOpFixedCertificateChainValidator() {
        super(null, null);
    }

    public CertificateValidity validate(X509Certificate certificate) {
        return CertificateValidity.valid();
    }
}