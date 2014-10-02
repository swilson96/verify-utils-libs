package uk.gov.ida.common.shared.security.verification;

import com.google.inject.Inject;
import uk.gov.ida.common.shared.security.X509CertificateFactory;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public NoOpCertificateChainValidator(
            PKIXParametersProvider pkixParametersProvider, X509CertificateFactory x509certificateFactory) {

        super(pkixParametersProvider, x509certificateFactory);
    }

    @Override
    public void validateOrThrow(X509Certificate certificate, KeyStore keyStore) {}
}