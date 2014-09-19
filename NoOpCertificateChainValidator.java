package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.shared.rest.common.CertificateDto;
import uk.gov.ida.shared.rest.common.transformers.CertificateDtoToX509CertificateTransformer;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public NoOpCertificateChainValidator(
            CertificateDtoToX509CertificateTransformer certificateTransformer, PKIXParametersProvider pkixParametersProvider) {

        super(certificateTransformer, pkixParametersProvider);
    }

    @Override
    public void validateOrThrow(CertificateDto certificate, IdaTrustStore trustStore) {}

    @Override
    public void validateOrThrow(X509Certificate certificate, IdaTrustStore trustStore) {}
}