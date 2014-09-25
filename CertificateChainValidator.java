package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.shared.rest.exceptions.CertificateChainValidationException;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.google.common.collect.ImmutableList.of;


public class CertificateChainValidator {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateChainValidator.class);

    private static final String PKIX_ALGORITHM = "PKIX";
    private static final String X509_CERTIFICATE_TYPE = "X.509";
    private final CertificateFactory certificateFactory;
    private final CertPathValidator certPathValidator;
    private final PKIXParametersProvider pkixParametersProvider;
    private final X509CertificateFactory x509certificateFactory;

    @Inject
    public CertificateChainValidator(
            PKIXParametersProvider pkixParametersProvider,
            X509CertificateFactory x509certificateFactory) {

        this.pkixParametersProvider = pkixParametersProvider;
        this.x509certificateFactory = x509certificateFactory;

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

    public void validateOrThrow(X509Certificate certificate, IdaTrustStore trustStore) {
        CertPath certificatePath;

        try {
            certificatePath = certificateFactory.generateCertPath(of(certificate));
        } catch (CertificateException e) {
            throw new CertificateChainValidationException("Error generating certificate path for certificate: " + getDnForCertificate(certificate), e);
        }

        try {
            certPathValidator.validate(certificatePath, pkixParametersProvider.getPkixParameters(trustStore));
        } catch (CertPathValidatorException e) {
            throw new CertificateChainValidationException(
                    "Certificate could not be chained to a trusted root CA certificate: " + getDnForCertificate(certificate),
                    e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("Unable to proceed in validating certificate chain: " + getDnForCertificate(certificate), e);
        }
    }

    public CertificateValidity validate(X509Certificate certificate, IdaTrustStore trustStore) {
        CertPath certificatePath;

        try {
            certificatePath = certificateFactory.generateCertPath(of(certificate));
        } catch (CertificateException e) {
            throw new CertificateChainValidationException("Error generating certificate path for certificate: " + getDnForCertificate(certificate), e);
        }

        try {
            certPathValidator.validate(certificatePath, pkixParametersProvider.getPkixParameters(trustStore));
        } catch (CertPathValidatorException e) {
            return CertificateValidity.invalid(e.getReason());
        } catch (InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("Unable to proceed in validating certificate chain: " + getDnForCertificate(certificate), e);
        }
        return CertificateValidity.valid();
    }

    public CertificateValidity validate(String x509String, IdaTrustStore trustStore) {
        X509Certificate x509Certificate = x509certificateFactory.createCertificate(x509String);
        return validate(x509Certificate, trustStore);
    }

    private String getDnForCertificate(X509Certificate certificate) {
        try {
            if (certificate != null && certificate.getSubjectDN() != null)
                return certificate.getSubjectDN().getName();
        } catch (RuntimeException e) {
            LOG.error("Failed to generate DN string for certificate", e);
        }
        return "";
    }
}