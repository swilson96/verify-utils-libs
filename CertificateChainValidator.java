package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import uk.gov.ida.shared.rest.exceptions.CertificateChainValidationException;
import uk.gov.ida.shared.rest.truststore.IdaTrustStore;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;

import static com.google.common.collect.ImmutableList.of;


public class CertificateChainValidator {
    private static final String PKIX_ALGORITHM = "PKIX";
    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private final PKIXParameters certPathParameters;
    private final CertificateFactory certificateFactory;
    private final CertPathValidator certPathValidator;

    @Inject
    public CertificateChainValidator(IdaTrustStore trustStore) {
        try {
            certPathParameters = new PKIXParameters(trustStore.getKeyStore().get());
        } catch (KeyStoreException | InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("There was an error reading from the trust store.", e);
        }
        certPathParameters.setRevocationEnabled(false);

        try {
            certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        } catch (CertificateException e) {
            throw new CertificateChainValidationException("Error retrieving X509 certificate factory instance.", e);
        }

        try {
            certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateChainValidationException("Error retrieving PKIX certificate path validator instance.", e);
        }
    }

    public void validate(X509Certificate certificate) {
        CertPath certificatePath;
        try {
            certificatePath = certificateFactory.generateCertPath(of(certificate));
        } catch (CertificateException e) {
            throw new CertificateChainValidationException("Error generating certificate path for certificate.", e);
        }

        try {
            certPathValidator.validate(certificatePath, certPathParameters);
        } catch (CertPathValidatorException e) {
            throw new CertificateChainValidationException(
                    "Certificate could not be chained to a trusted root CA certificate.",
                    e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("Unable to proceed in validating certificate chain.", e);
        }
    }
}