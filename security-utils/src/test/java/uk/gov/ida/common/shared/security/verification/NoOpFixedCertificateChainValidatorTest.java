package uk.gov.ida.common.shared.security.verification;

import org.junit.Test;
import org.mockito.Mock;

import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;

public class NoOpFixedCertificateChainValidatorTest {

    @Mock
    private X509Certificate x509Certificate;

    @Test
    public void shouldReturnValidValidityForAnyCertificate() {
        final NoOpFixedCertificateChainValidator noOpFixedCertificateChainValidator = new NoOpFixedCertificateChainValidator();
        final CertificateValidity certificateValidity = noOpFixedCertificateChainValidator.validate(x509Certificate);

        assertThat(certificateValidity.isValid()).isTrue();
    }
}