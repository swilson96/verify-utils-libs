package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.shared.rest.common.transformers.CertificateDtoToX509CertificateTransformer;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public NoOpCertificateChainValidator(
            IdaTrustStore trustStore,
            CertificateDtoToX509CertificateTransformer certificateTransformer) {

        super(certificateTransformer);
    }

    public void validate(X509Certificate certificate) {
    }
}