package uk.gov.ida.shared.rest.config.verification;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;


public class CertificateChainVerifier {
    public final String PKIX_ALGORITHM = "PKIX";
    public final String X509_CERTIFICATE_TYPE = "X.509";

    /**
     * Verifies the integrity of a certificate chain.
     * @param certificateChain A list (or chain) of certificates to verify. Order is important - the first in the list should be at the bottom of the chain and the last should be at the top (i.e. the last certificate in the list should be signed directly by the root CA certificate.
     * @param caCertificate A root CA certificate, at the top of the signing chain.
     * @throws Exception If the certificate chain could not be verified with the specified root CA certificate.
     */
    public void verify(
            List<X509Certificate> certificateChain,
            X509Certificate caCertificate) throws Exception {

        TrustAnchor anchor = new TrustAnchor(caCertificate, null);

        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        CertPath certPath = certificateFactory.generateCertPath(certificateChain);

        PKIXParameters certPathParameters = new PKIXParameters(Collections.singleton(anchor));
        certPathParameters.setRevocationEnabled(false);

        CertPathValidator certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);

        certPathValidator.validate(certPath, certPathParameters);
    }
}