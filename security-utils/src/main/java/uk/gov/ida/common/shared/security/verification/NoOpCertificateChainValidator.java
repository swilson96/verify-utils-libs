package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public NoOpCertificateChainValidator(
            PKIXParametersProvider pkixParametersProvider, X509CertificateFactory x509certificateFactory) {

        super(pkixParametersProvider, x509certificateFactory);
    }

    @Override
    public void validateOrThrow(X509Certificate certificate, IdaTrustStore trustStore) {}
}