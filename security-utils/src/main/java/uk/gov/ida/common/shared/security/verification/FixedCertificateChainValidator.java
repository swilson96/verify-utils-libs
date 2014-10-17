package uk.gov.ida.common.shared.security.verification;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;
import java.security.cert.X509Certificate;


public class FixedCertificateChainValidator {

    private static final Logger LOG = LoggerFactory.getLogger(FixedCertificateChainValidator.class);

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