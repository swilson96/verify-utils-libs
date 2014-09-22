package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.shared.configuration.PublicKeyConfiguration;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration, ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getPrimaryEncryptionCertificateValue();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getEncryptionCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration, ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getPrimaryEncryptionCertificateValue();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getEncryptionCertificateValue_shouldReturnAppropriateCertificateValue() throws UnsupportedEncodingException {
        String secondaryCertificateWithoutHeader = "sfjkvjdfbkbdfjbmxbfsjbdfjbdfjbjbfdjbfdkjhbkfbdj";
        when(publicKeyFileInputStreamFactory.createInputStream("primaryUri")).thenReturn(new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8")));
        when(publicKeyFileInputStreamFactory.createInputStream("secondaryUri")).thenReturn(new ByteArrayInputStream(secondaryCertificateWithoutHeader.getBytes("UTF-8")));

        PublicKeyConfiguration secondaryPublicKeyConfiguration = mock(PublicKeyConfiguration.class);

        when(publicKeyConfiguration.getKeyUri()).thenReturn("primaryUri");
        when(secondaryPublicKeyConfiguration.getKeyUri()).thenReturn("secondaryUri");

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,secondaryPublicKeyConfiguration, ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);

        String primaryEncryptionCertificateValue = certificateStore.getPrimaryEncryptionCertificateValue();
        String secondaryEncryptionCertificateValue = certificateStore.getSecondaryEncryptionCertificateValue();

        assertThat(primaryEncryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
        assertThat(secondaryEncryptionCertificateValue).isEqualTo(secondaryCertificateWithoutHeader);
    }

    @Test
    public void getSigningCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITH_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration, ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        List<String> signingCertificateValues = certificateStore.getSigningCertificateValues();

        assertThat(signingCertificateValues).hasSize(1);

        String primaryCertificate = signingCertificateValues.get(0);
        assertThat(primaryCertificate).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getSigningCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream1).thenReturn(byteArrayInputStream2);

        CertificateStore certificateStore = new CertificateStore(publicKeyConfiguration,publicKeyConfiguration, ImmutableList.of(publicKeyConfiguration, publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        List<String> signingCertificateValues = certificateStore.getSigningCertificateValues();


        assertThat(signingCertificateValues).hasSize(2);

        String primarySigningCertificate = signingCertificateValues.get(0);
        assertThat(primarySigningCertificate).isEqualTo(CERTIFICATE_WITHOUT_HEADER);

        String secondarySigningCertificate = signingCertificateValues.get(1);
        assertThat(secondarySigningCertificate).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }


}
