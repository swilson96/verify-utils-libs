package uk.gov.ida.common.shared.security.verification;

import com.google.inject.Inject;

import java.security.KeyStore;
import java.security.cert.X509Certificate;


public class FixedCertificateChainValidator {
    private final KeyStore trustStore;
    private final CertificateChainValidator certificateChainValidator;

    @Inject
    public FixedCertificateChainValidator(
            KeyStore trustStore,
            CertificateChainValidator certificateChainValidator) {
        this.trustStore = trustStore;
        this.certificateChainValidator = certificateChainValidator;
    }

    public void validate(X509Certificate certificate) {
        certificateChainValidator.validateOrThrow(certificate, trustStore);
    }
}