package uk.gov.ida.common.shared.security.verification;

import javax.inject.Inject;
import uk.gov.ida.common.shared.security.X509CertificateFactory;

public class OCSPCertificateChainValidator extends CertificateChainValidator {

    @Inject
    public OCSPCertificateChainValidator(
            OCSPPKIXParametersProvider parametersProvider, X509CertificateFactory x509certificateFactory) {
        super(parametersProvider, x509certificateFactory);
    }
}
