package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.shared.rest.truststore.IdaTrustStore;

import java.security.cert.X509Certificate;

public class NoOpCertificateChainValidator extends CertificateChainValidator{

    @Inject
    public NoOpCertificateChainValidator(IdaTrustStore trustStore) {
        super(trustStore);
    }

    public void validate(X509Certificate certificate) {
    }
}