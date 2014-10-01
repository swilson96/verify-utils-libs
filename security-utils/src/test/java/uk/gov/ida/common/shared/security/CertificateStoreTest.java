package uk.gov.ida.common.shared.security;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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

        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getEncryptionCertificates().get(0).getCertificate();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getEncryptionCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");

        String encryptionCertificateValue = certificateStore.getEncryptionCertificates().get(0).getCertificate();

        assertThat(encryptionCertificateValue.contains("BEGIN")).isEqualTo(false);
        assertThat(encryptionCertificateValue.contains("END")).isEqualTo(false);
        assertThat(encryptionCertificateValue).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getEncryptionCertificateValue_shouldHandleMultipleCertificateValues() throws UnsupportedEncodingException {
        String secondCertificateWithoutHeader = "sfjkvjdfbkbdfjbmxbfsjbdfjbdfjbjbfdjbfdkjhbkfbdj";
        when(publicKeyFileInputStreamFactory.createInputStream("firstUri")).thenReturn(new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8")));
        when(publicKeyFileInputStreamFactory.createInputStream("secondUri")).thenReturn(new ByteArrayInputStream(secondCertificateWithoutHeader.getBytes("UTF-8")));

        PublicKeyConfiguration anotherPublicKeyConfiguration = mock(PublicKeyConfiguration.class);

        when(publicKeyConfiguration.getKeyUri()).thenReturn("firstUri");
        when(publicKeyConfiguration.getKeyName()).thenReturn("first");
        when(anotherPublicKeyConfiguration.getKeyUri()).thenReturn("secondUri");
        when(anotherPublicKeyConfiguration.getKeyName()).thenReturn("second");

        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration, anotherPublicKeyConfiguration), ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);

        final List<Certificate> encryptionCertificates = certificateStore.getEncryptionCertificates();
        String firstEncryptionCertificateValue = encryptionCertificates.get(0).getCertificate();
        String secondEncryptionCertificateValue = encryptionCertificates.get(1).getCertificate();

        assertThat(ImmutableSet.of(firstEncryptionCertificateValue, secondEncryptionCertificateValue))
                .isEqualTo(ImmutableSet.of(CERTIFICATE_WITHOUT_HEADER, secondCertificateWithoutHeader));
    }

    @Test
    public void getSigningCertificateValue_shouldStripOutHeadersIfPresent() throws UnsupportedEncodingException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CERTIFICATE_WITH_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri")).thenReturn(byteArrayInputStream);

        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri");
        when(publicKeyConfiguration.getKeyName()).thenReturn("primary");

        List<Certificate> signingCertificateValues = certificateStore.getSigningCertificates();

        assertThat(signingCertificateValues).hasSize(1);

        Certificate primaryCertificate = signingCertificateValues.get(0);
        assertThat(primaryCertificate.getIssuerId()).isEqualTo("primary");
        assertThat(primaryCertificate.getCertificate()).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }

    @Test
    public void getSigningCertificateValue_shouldReturnOriginalCertificateIfHeadersAreAbsent() throws UnsupportedEncodingException {
        PublicKeyConfiguration secondaryPublicKeyConfiguration = mock(PublicKeyConfiguration.class);

        ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(CERTIFICATE_WITHOUT_HEADER.getBytes("UTF-8"));
        when(publicKeyFileInputStreamFactory.createInputStream("uri1")).thenReturn(byteArrayInputStream1);
        when(publicKeyFileInputStreamFactory.createInputStream("uri2")).thenReturn(byteArrayInputStream2);

        CertificateStore certificateStore = new CertificateStore(ImmutableList.of(publicKeyConfiguration), ImmutableList.of(publicKeyConfiguration, secondaryPublicKeyConfiguration), publicKeyFileInputStreamFactory);
        when(publicKeyConfiguration.getKeyUri()).thenReturn("uri1");
        when(publicKeyConfiguration.getKeyName()).thenReturn("primary");
        when(secondaryPublicKeyConfiguration.getKeyUri()).thenReturn("uri2");
        when(secondaryPublicKeyConfiguration.getKeyName()).thenReturn("secondary");

        List<Certificate> signingCertificateValues = certificateStore.getSigningCertificates();

        assertThat(signingCertificateValues).hasSize(2);

        Certificate primarySigningCertificate = signingCertificateValues.get(0);
        assertThat(primarySigningCertificate.getIssuerId()).isEqualTo("primary");
        assertThat(primarySigningCertificate.getCertificate()).isEqualTo(CERTIFICATE_WITHOUT_HEADER);

        Certificate secondarySigningCertificate = signingCertificateValues.get(1);
        assertThat(secondarySigningCertificate.getIssuerId()).isEqualTo("secondary");
        assertThat(secondarySigningCertificate.getCertificate()).isEqualTo(CERTIFICATE_WITHOUT_HEADER);
    }


}
