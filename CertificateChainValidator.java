package uk.gov.ida.shared.rest.config.verification;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import uk.gov.ida.shared.rest.truststore.DependentServiceSSLTrustStore;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;


public class CertificateChainValidator {
    public final String PKIX_ALGORITHM = "PKIX";
    public final String X509_CERTIFICATE_TYPE = "X.509";
    private DependentServiceSSLTrustStore trustStore;

    @Inject
    public CertificateChainValidator() {
    }

    public CertificateChainValidator(DependentServiceSSLTrustStore trustStore) {
        this.trustStore = trustStore;
    }

    public void validate(X509Certificate certificate) throws Exception {

        PKIXParameters certPathParameters = new PKIXParameters(trustStore.getKeyStore().get());
        final ImmutableList<X509Certificate> certificateChain = ImmutableList.of(certificate);

        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        CertPath certPath = certificateFactory.generateCertPath(certificateChain);

        certPathParameters.setRevocationEnabled(false);

        CertPathValidator certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);

        certPathValidator.validate(certPath, certPathParameters);
    }
}