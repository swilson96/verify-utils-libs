package uk.gov.ida.shared.rest.config.verification;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import uk.gov.ida.shared.rest.common.ExceptionType;
import uk.gov.ida.shared.rest.exceptions.ApplicationErrorException;
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
import java.util.UUID;

import static com.google.common.collect.ImmutableList.of;


public class CertificateChainValidator {
    public final String PKIX_ALGORITHM = "PKIX";
    public final String X509_CERTIFICATE_TYPE = "X.509";
    public PKIXParameters certPathParameters;
    public CertificateFactory certificateFactory;
    public CertPathValidator certPathValidator;

    @Inject
    public CertificateChainValidator(IdaTrustStore trustStore) {
        try {
            certPathParameters = new PKIXParameters(trustStore.getKeyStore().get());
        } catch (KeyStoreException | InvalidAlgorithmParameterException e) {
            throw Throwables.propagate(e);
        }
        certPathParameters.setRevocationEnabled(false);

        try {
            certificateFactory = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE);
        } catch (CertificateException e) {
            throw Throwables.propagate(e);
        }

        try {
            certPathValidator = CertPathValidator.getInstance(PKIX_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw Throwables.propagate(e);
        }
    }

    public void validate(X509Certificate certificate) {
        CertPath certificatePath;
        try {
            certificatePath = certificateFactory.generateCertPath(of(certificate));
        } catch (CertificateException e) {
            throw Throwables.propagate(e);
        }

        try {
            certPathValidator.validate(certificatePath, certPathParameters);
        } catch (CertPathValidatorException e) {
            throw ApplicationErrorException.createUnauditedException(ExceptionType.UNCHAINED_CERT, UUID.randomUUID(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw Throwables.propagate(e);
        }
    }
}