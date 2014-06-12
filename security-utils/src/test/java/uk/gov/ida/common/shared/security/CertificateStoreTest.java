package uk.gov.ida.common.shared.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CertificateStoreTest {

    @Mock
    PublicKeyFileInputStreamFactory publicKeyFileInputStreamFactory;
    @Mock
    PublicKeyConfiguration publicKeyConfiguration;

    public static final String CERTIFICATE_WITHOUT_HEADER = "MIIEMzCCAxugAwIBAgIQKrJnJNMkTeKeknvPQb2DANBgkqhkiG9w0BAQsFADBG";
    public static final String CERTIFICATE_WITH_HEADER = "-----BEGIN CERTIFICATE----- "+ CERTIFICATE_WITHOUT_HEADER +"-----END CERTIFICATE-----";

    @Test
    public void getEncryptionCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITH_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration,publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getEncryptionCertificateValue();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getEncryptionCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration,publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getEncryptionCertificateValue();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getSigningCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITH_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration,publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String signingCertificateValue = certificateStore.getSigningCertificateValue();

        assertThat(signingCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(signingCertificateValue.contains("END")).isEqualTo(false);
        assertThat(signingCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getSigningCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration,publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String signingCertificateValue = certificateStore.getSigningCertificateValue();

        assertThat(signingCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(signingCertificateValue.contains("END")).isEqualTo(false);
        assertThat(signingCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }
}
