package uk.gov.ida.matchingserviceadapter.configuration.verification;

import com.google.inject.Inject;
import uk.gov.ida.matchingserviceadapter.rest.transformers.CertificateDtoToX509CertificateTransformer;
import uk.gov.ida.matchingserviceadapter.rest.truststore.IdaTrustStore;

import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public NoOpCertificateChainValidator(
            IdaTrustStore trustStore,
            CertificateDtoToX509CertificateTransformer certificateTransformer) {

        super(trustStore, certificateTransformer);
    }

    public void validate(X509Certificate certificate) {
    }
}