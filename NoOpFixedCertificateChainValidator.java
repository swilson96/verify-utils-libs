package uk.gov.ida.matchingserviceadapter.configuration.verification;

import com.google.inject.Inject;

import java.security.cert.X509Certificate;

public class NoOpFixedCertificateChainValidator extends FixedCertificateChainValidator {

    @Inject
    public NoOpFixedCertificateChainValidator() {

        super(null, null);
    }

    public void validate(X509Certificate certificate) {
    }
}