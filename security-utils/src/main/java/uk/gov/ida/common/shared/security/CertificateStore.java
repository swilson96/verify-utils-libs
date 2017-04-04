package uk.gov.ida.common.shared.security;

import uk.gov.ida.common.shared.configuration.DeserializablePublicKeyConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CertificateStore {
    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    private final List<DeserializablePublicKeyConfiguration> publicEncryptionKeyConfigurations;
    private final List<DeserializablePublicKeyConfiguration> publicSigningKeyConfigurations;

    public CertificateStore(
            List<DeserializablePublicKeyConfiguration> publicEncryptionKeyConfigurations,
            List<DeserializablePublicKeyConfiguration> publicSigningKeyConfiguration) {

        this.publicEncryptionKeyConfigurations = publicEncryptionKeyConfigurations;
        this.publicSigningKeyConfigurations = publicSigningKeyConfiguration;
    }

    public List<Certificate> getEncryptionCertificates() {
        List<Certificate> certs = new ArrayList<>();
        for (DeserializablePublicKeyConfiguration certConfig : publicEncryptionKeyConfigurations) {
            certs.add(new Certificate(certConfig.getName(), stripHeaders(certConfig.getCert()), Certificate.KeyUse.Encryption));
        }
        return certs;
    }

    public List<Certificate> getSigningCertificates() {
        List<Certificate> certs = new ArrayList<>();
        for (DeserializablePublicKeyConfiguration certConfig : publicSigningKeyConfigurations) {
            certs.add(new Certificate(certConfig.getName(), stripHeaders(certConfig.getCert()), Certificate.KeyUse.Signing));
        }
        return certs;
    }

    private String stripHeaders(final String originalCertificate) {
        String strippedCertificate = originalCertificate;
        if (originalCertificate.contains(BEGIN_CERTIFICATE)){
            strippedCertificate = originalCertificate.replace(BEGIN_CERTIFICATE, "");
        }
        if (originalCertificate.contains(END_CERTIFICATE)){
            strippedCertificate = strippedCertificate.replace(END_CERTIFICATE, "");
        }
        return strippedCertificate.replace(" ","");
    }
}
