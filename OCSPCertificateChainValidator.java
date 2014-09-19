package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.shared.rest.common.transformers.CertificateDtoToX509CertificateTransformer;

public class OCSPCertificateChainValidator extends CertificateChainValidator{

    @Inject
    public OCSPCertificateChainValidator(
            CertificateDtoToX509CertificateTransformer certificateDtoToX509CertificateTransformer, OCSPPKIXParametersProvider parametersProvider) {
        super(certificateDtoToX509CertificateTransformer, parametersProvider);
    }
}
