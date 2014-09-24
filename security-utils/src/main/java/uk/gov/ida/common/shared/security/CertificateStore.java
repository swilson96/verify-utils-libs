package uk.gov.ida.common.shared.security;

import org.apache.commons.codec.binary.StringUtils;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Throwables.propagate;

public class CertificateStore {
    public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
    private final PublicKeyConfiguration primaryPublicEncryptionKeyConfiguration;
    private final PublicKeyConfiguration secondaryPublicEncryptionKeyConfiguration;
    private final List<PublicKeyConfiguration> publicSigningKeyConfigurations;
    private final PublicKeyInputStreamFactory publicKeyInputStreamFactory;

    public CertificateStore(
            PublicKeyConfiguration primaryPublicEncryptionKeyConfiguration,
            PublicKeyConfiguration secondaryPublicEncryptionKeyConfiguration,
            List<PublicKeyConfiguration> publicSigningKeyConfiguration,
            PublicKeyInputStreamFactory publicKeyInputStreamFactory) {

        this.primaryPublicEncryptionKeyConfiguration = primaryPublicEncryptionKeyConfiguration;
        this.secondaryPublicEncryptionKeyConfiguration = secondaryPublicEncryptionKeyConfiguration;
        this.publicSigningKeyConfigurations = publicSigningKeyConfiguration;
        this.publicKeyInputStreamFactory = publicKeyInputStreamFactory;
    }

    public String getPrimaryEncryptionCertificateValue() {
        return getCertificate(primaryPublicEncryptionKeyConfiguration);
    }

    public String getSecondaryEncryptionCertificateValue() {
        return getCertificate(secondaryPublicEncryptionKeyConfiguration);
    }

    public List<Certificate> getSigningCertificateValues() {
        List<Certificate> certs = new ArrayList<>();
        for (PublicKeyConfiguration certConfig : publicSigningKeyConfigurations) {
            certs.add(new Certificate(certConfig.getKeyName(), getCertificate(certConfig), Certificate.KeyUse.Signing));
        }
        return certs;
    }

    private String getCertificate(PublicKeyConfiguration configuration) {
        try (InputStream inputStream = publicKeyInputStreamFactory.createInputStream(configuration.getKeyUri())) {

            String originalCertificate = StringUtils.newStringUtf8(FileUtils.readStream(inputStream));
            return stripHeaders(originalCertificate);
        } catch (IOException e) {
            throw propagate(e);
        }
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
