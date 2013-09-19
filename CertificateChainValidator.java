package uk.gov.ida.shared.rest.config.verification;

import com.google.common.collect.ImmutableList;
import uk.gov.ida.shared.rest.truststore.DependentServiceSSLTrustStore;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;


public class CertificateChainValidator {
    public final String PKIX_ALGORITHM = "PKIX";
    public final String X509_CERTIFICATE_TYPE = "X.509";

    public void validate(
            X509Certificate certificate,
            DependentServiceSSLTrustStore trustStore) throws Exception {

        PKIXParameters certPathParameters = new PKIXParameters(trustStore.getKeyStore().get());
        final ImmutableList<X509Certificate> certificateChain = ImmutableList.of(certificate);

        validate(certPathParameters, certificateChain);
    }

    private void validate(
            PKIXParameters certPathParameters,
            List<X509Certificate> certificateChain) throws CertificateException, NoSuchAlgorithmException, CertPathValidatorException, InvalidAlgorithmParameterException {

        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        CertPath certPath = certificateFactory.generateCertPath(certificateChain);

        certPathParameters.setRevocationEnabled(false);

        CertPathValidator certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);

        certPathValidator.validate(certPath, certPathParameters);
    }
}