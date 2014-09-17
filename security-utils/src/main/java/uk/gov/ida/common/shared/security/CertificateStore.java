package uk.gov.ida.common.shared.security;

import org.apache.commons.codec.binary.StringUtils;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Throwables.propagate;

public class CertificateStore {
    public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
    private final PublicKeyConfiguration primaryPublicEncryptionKeyConfiguration;
    private final PublicKeyConfiguration secondaryPublicEncryptionKeyConfiguration;
    private final PublicKeyConfiguration publicSigningKeyConfiguration;
    private final PublicKeyInputStreamFactory publicKeyInputStreamFactory;

    public CertificateStore(
            PublicKeyConfiguration primaryPublicEncryptionKeyConfiguration,
            PublicKeyConfiguration secondaryPublicEncryptionKeyConfiguration,
            PublicKeyConfiguration publicSigningKeyConfiguration,
            PublicKeyInputStreamFactory publicKeyInputStreamFactory) {

        this.primaryPublicEncryptionKeyConfiguration = primaryPublicEncryptionKeyConfiguration;
        this.secondaryPublicEncryptionKeyConfiguration = secondaryPublicEncryptionKeyConfiguration;
        this.publicSigningKeyConfiguration = publicSigningKeyConfiguration;
        this.publicKeyInputStreamFactory = publicKeyInputStreamFactory;
    }

    public String getPrimaryEncryptionCertificateValue() {
        return getCertificate(primaryPublicEncryptionKeyConfiguration);
    }

    public String getSecondaryEncryptionCertificateValue() {
        return getCertificate(secondaryPublicEncryptionKeyConfiguration);
    }

    public String getSigningCertificateValue() {
        return getCertificate(publicSigningKeyConfiguration);
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
