package uk.gov.ida.matchingserviceadapter.rest.configuration.verification;

import com.google.common.base.Throwables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.matchingserviceadapter.configuration.verification.CertificateChainValidator;
import uk.gov.ida.matchingserviceadapter.security.CertificateFactory;
import uk.gov.ida.matchingserviceadapter.rest.common.CertificateDto;
import uk.gov.ida.matchingserviceadapter.rest.common.transformers.CertificateDtoToX509CertificateTransformer;
import uk.gov.ida.matchingserviceadapter.exceptions.CertificateChainValidationException;
import uk.gov.ida.matchingserviceadapter.rest.truststore.IdaTrustStore;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.hub.shared.test.builders.CertificateDtoBuilder.aCertificateDto;

@RunWith(MockitoJUnitRunner.class)
public class CertificateChainValidatorTest {

    private CertificateFactory certificateFactory;
    private CertificateChainValidator certificateChainValidator;

    @Mock
    private CertificateDtoToX509CertificateTransformer certificateDtoToX509CertificateTransformer;

    @Before
    public void setUp() throws Exception {
        certificateFactory = new CertificateFactory();
        certificateChainValidator = new CertificateChainValidator(getTrustStore(), certificateDtoToX509CertificateTransformer);
    }

    @Test
    public void validate_shouldPassACertSignedByRootCACertInTrustStore() throws Exception {
        final X509Certificate intermediaryCACertificate = certificateFactory.createCertificate(intermediaryCACertString);

        certificateChainValidator.validate(intermediaryCACertificate);
    }

    @Test
    public void validate_shouldPassACertSignedByAnIntermediaryCACertSignedByRootCACertInTrustStore() throws Exception {
        final X509Certificate encryptionCertificate = certificateFactory.createCertificate(this.encryptionCertString);

        certificateChainValidator.validate(encryptionCertificate);
    }

    @Test
    public void validate_shouldFailACertSignedByAnUnknownRootCACert() throws Exception {
        final X509Certificate otherChildCertificate =
                certificateFactory.createCertificate(childSignedByOtherRootCAString);

        assertExceptionMessage(
                certificateChainValidator,
                otherChildCertificate,
                CertificateChainValidationException.class,
                "Certificate could not be chained to a trusted root CA certificate: EMAILADDRESS=mark.taylor1, CN=127.0.0.1, OU=GDS, O=Cabinet Office, L=London, ST=London, C=GB"
        );
    }

    @Test
    public void validate_shouldHandleCertificateDtos() throws Exception {
        final CertificateDto certificateDto = aCertificateDto().withCertificate(this.encryptionCertString).build();
        when(certificateDtoToX509CertificateTransformer.transform(any(CertificateDto.class)))
                .thenReturn(certificateFactory.createCertificate(this.encryptionCertString));

        certificateChainValidator.validate(certificateDto);

        verify(certificateDtoToX509CertificateTransformer).transform(certificateDto);
    }

    @Test(expected = CertificateChainValidationException.class)
    public void validate_shouldWrapCertificateExceptionsGeneratedByTransformer() throws Exception {
        when(certificateDtoToX509CertificateTransformer.transform(any(CertificateDto.class))).thenThrow(new CertificateException());

        certificateChainValidator.validate(aCertificateDto().build());
    }

    private void assertExceptionMessage(
            CertificateChainValidator validator,
            X509Certificate certificate,
            Class exceptionClass,
            String value) {

        try {
            validator.validate(certificate);
        } catch (Exception e) {
            assertThat(e.getClass()).isEqualTo(exceptionClass);
            assertThat(e.getMessage()).isEqualTo(value);
            return;
        }
        fail("Should have thrown exception.");
    }

    public IdaTrustStore getTrustStore() {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());

            InputStream inputStream = null;
            try {
                inputStream = getClass().getClassLoader().getResourceAsStream("ida_truststore.ts");
                ks.load(inputStream, "puppet".toCharArray());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw Throwables.propagate(e);
        }
        return new IdaTrustStore(ks);
    }

    private final String intermediaryCACertString = "-----BEGIN CERTIFICATE-----\n" +
            "MIIEizCCAnOgAwIBAgIQOIGzwEArw68viPqnjAjpYzANBgkqhkiG9w0BAQsFADBQ\n" +
            "MQswCQYDVQQGEwJHQjEXMBUGA1UEChMOQ2FiaW5ldCBPZmZpY2UxDDAKBgNVBAsT\n" +
            "A0dEUzEaMBgGA1UEAxMRSURBUCBUZXN0IFJvb3QgQ0EwHhcNMTMwODAyMDAwMDAw\n" +
            "WhcNMTgwODAxMjM1OTU5WjBLMQswCQYDVQQGEwJHQjEXMBUGA1UEChMOQ2FiaW5l\n" +
            "dCBPZmZpY2UxDDAKBgNVBAsTA0dEUzEVMBMGA1UEAxMMSURBUCBUZXN0IENBMIIB\n" +
            "IjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuMMDG1WFGOF4kE9fvwAt9DwE\n" +
            "FvtPSHy61emHIw8qLO8yY/9sv1pUjSPVnkzO6tEMKlgX7l2LF1JkvTWYrn1C6uIn\n" +
            "gx7rMKjbWLGNDLKlL3gpTND93zDWD5OEqSoV1OZyVnO8ryIhB642kE8uFhqPTQym\n" +
            "uZcK55GxIo83AeZ2Ct5iAmLUOuYTvmY9NligV+cwfi0vvT6yLgiLjGiGtxYWnW4k\n" +
            "TULoDl9F+KZNgYKI42eRxNgQn4BlU7vX6ZXjvFy0goGSsphae1nCXTmncFXMV+JA\n" +
            "oGlPZG5AesxJytTAJRZ4hTsToaeaimAhqEIkWwbzNjfYh4qNCogdmSUxBjPi/QID\n" +
            "AQABo2YwZDAOBgNVHQ8BAf8EBAMCAQYwEgYDVR0TAQH/BAgwBgEB/wIBADAdBgNV\n" +
            "HQ4EFgQUahFNZFDf3mNvV6GIH/gDlQ4iBLQwHwYDVR0jBBgwFoAUY1DlEvzqocu7\n" +
            "imr+k7WcwjGZRDYwDQYJKoZIhvcNAQELBQADggIBAGx8+5MpS9r8UpSGq2rAqYNO\n" +
            "UuBY8zyfL1+SczsE5FyG8ofjyKI52fvBwqp+m9fCZSHX6FV4+VaDeZi+Hw24Rtxs\n" +
            "6eOee8f7l9n4VsGiGy3ojYMDQkSgodKW4imJJGt32guwc0CGtaeBRJ9VmGdb7xOT\n" +
            "3ahiaQlW7SUq58GNVFgDUaDxFmwmJceT/L7sw5wnrXg0BVte3SILV6UZaL6Ls/Xb\n" +
            "TdF4oBs5qBWjhhbv26zEOFm6JnorHt1vqSrIIlspwuRNU8DSb1CP7sNtigimUv8B\n" +
            "HxKPI3NSMPk/PpfmJT+dvzBaUe582X84ZJE/EIlYA1c5PloZ1M7iPEemDTGZP2Ae\n" +
            "9ce//aKjqueKQV7YvmnZIbRO8R5k7YK/i6M/FxVAArM821/ohTVuieakR9D3zxjw\n" +
            "LkCLr2WEpyoHcafbOXkSyPLy7Ee+n2yFblpAVGJwvYzaHCDB7eZ5qDVByjgFtO8t\n" +
            "bJBpB0yLqfxVBOR3WjbbmPXIbbZQ84PHsvEtu3tZPP1Ii7iGm1zgLaJpxP7yBmvG\n" +
            "ptLaG0AHdm6AJQkWaQnUIh0fy3K8BuKxZxg5kLgvVi51FONMobc14LHDvaStbwpk\n" +
            "nFCrhgY3iD8kAVy0MnnA1BC68DD8JxGGuVwb48fi/7GnMi5ais74TwmlmlDfN4Ev\n" +
            "/mDpoOgfBgFzahzVKCMZ\n" +
            "-----END CERTIFICATE-----";

    private final String encryptionCertString = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDwTCCAqmgAwIBAgIQd/Dw6ZLsVP1PolV4c7YCFjANBgkqhkiG9w0BAQsFADBL\n" +
            "MQswCQYDVQQGEwJHQjEXMBUGA1UEChMOQ2FiaW5ldCBPZmZpY2UxDDAKBgNVBAsT\n" +
            "A0dEUzEVMBMGA1UEAxMMSURBUCBUZXN0IENBMB4XDTEzMDkxMTAwMDAwMFoXDTE1\n" +
            "MDkxMTIzNTk1OVowezELMAkGA1UEBhMCR0IxDzANBgNVBAgTBkxvbmRvbjEPMA0G\n" +
            "A1UEBxMGTG9uZG9uMRcwFQYDVQQKFA5DYWJpbmV0IE9mZmljZTEMMAoGA1UECxQD\n" +
            "R0RTMSMwIQYDVQQDExpDb21wYW5pZXMgSG91c2UgRW5jcnlwdGlvbjCBnzANBgkq\n" +
            "hkiG9w0BAQEFAAOBjQAwgYkCgYEArPnWzjr7vnOjEre2lNCKHBmhRqEfF7XC+gc5\n" +
            "Em5IEFnjdVJHBR9pQLjm+o3WwUd21U+WL5lSLLN7VUmKT3lmd4kMlmGAcvS6PiQc\n" +
            "SG3Sfx4pKl5le6NblE8rhjXRvcRDmGc7cfvFhVbb5wf6oTid8OYy5hgujiXhZ+oS\n" +
            "UmyVPw0CAwEAAaOB9DCB8TAMBgNVHRMBAf8EAjAAMFUGA1UdHwROMEwwSqBIoEaG\n" +
            "RGh0dHA6Ly9vbnNpdGVjcmwudmluby5idC5jby51ay9DYWJpbmV0T2ZmaWNlSURB\n" +
            "UFRlc3RDQS9MYXRlc3RDUkwuY3JsMA4GA1UdDwEB/wQEAwIFIDAdBgNVHQ4EFgQU\n" +
            "i/GaO7jjEodb1EpozzXteLJ1qm4wHwYDVR0jBBgwFoAUahFNZFDf3mNvV6GIH/gD\n" +
            "lQ4iBLQwOgYIKwYBBQUHAQEELjAsMCoGCCsGAQUFBzABhh5odHRwOi8vb2NzcC10\n" +
            "ZXN0LnRydXN0d2lzZS5jb20wDQYJKoZIhvcNAQELBQADggEBADXqahIvSQqvRfGN\n" +
            "aKGvAYD9Dc45Myn8iGTimDTPXlT13ceh3e1A61r6JSoW+++KYs7OkAtejxFTla/h\n" +
            "FZIO5pctH59qiD4jEMxX5CeDO36yI0DNGJws6oIfUh8bsZHnoCuzFGnOU2DGfdWh\n" +
            "W9QFthanXwJqjghl+J4cFwNpO4ws9+yyDfkyFOKcIBHu9jM4Lf9Y8cgYFa9Cry5p\n" +
            "mjzqo/mXglm5NT8hIaEhcs8aAY+gR9VWybBURuyrOuVl0REOAt7HdWm/G3UNv/I/\n" +
            "TjUAUEGMGlBuINsQiypWHf8Xr6P6POQ4igRhD0t6RFCSB90XYKjb5DuIwXTkDt04\n" +
            "GUwSFoQ=\n" +
            "-----END CERTIFICATE-----";

    private final String childSignedByOtherRootCAString = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDjzCCAncCCQDwHVkOSrcxeDANBgkqhkiG9w0BAQUFADCBijELMAkGA1UEBhMC\n" +
            "R0IxDzANBgNVBAgTBkxvbmRvbjEPMA0GA1UEBxMGTG9uZG9uMRcwFQYDVQQKEw5D\n" +
            "YWJpbmV0IE9mZmljZTEMMAoGA1UECxMDR0RTMRUwEwYDVQQDEwxNYXJrIFJvb3Qg\n" +
            "Q0ExGzAZBgkqhkiG9w0BCQEWDG1hcmsudGF5bG9yMTAeFw0xMzA5MTgxMzI1MDha\n" +
            "Fw0xNTAxMzExMzI1MDhaMIGHMQswCQYDVQQGEwJHQjEPMA0GA1UECBMGTG9uZG9u\n" +
            "MQ8wDQYDVQQHEwZMb25kb24xFzAVBgNVBAoTDkNhYmluZXQgT2ZmaWNlMQwwCgYD\n" +
            "VQQLEwNHRFMxEjAQBgNVBAMTCTEyNy4wLjAuMTEbMBkGCSqGSIb3DQEJARYMbWFy\n" +
            "ay50YXlsb3IxMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAttDMf6ai\n" +
            "wdVR5/qqSUSzK5Kh2/7e0tMrmD78lnO3is0gAGFJN2Lh9nqMfk/k3WptsgKT62sz\n" +
            "2xBYoLE2TLkusdleTy3dEXOjAvQiC3RhLcFdU98eYteR4kaWPzQELwThr4g4jW70\n" +
            "BdQbiAU+X3GekV2InmSD+kugbqTGFQ1b0RW/7ekE49KCjvWMvXGijVqWcun3Rpls\n" +
            "LMjHi1s9VonNCnYygIjHKfckQS11mrgMLnH6JgYmysT7jILxZUpybjEXuoGXfgaC\n" +
            "fl1nTz9UpUl8Sv35avhPWJ2GEtSyaKv1CVUL2+aCHKnMsj2zz+yz8gavjCHIfjFs\n" +
            "6EJ25zXkatRccQIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQBqRILBd58THiJmc5Nk\n" +
            "EPC2HMuhq4uG1MDQbT1jgypos190AtYfTxrmPSaWGOZTgIbHUFcLH2a2YyApsgbD\n" +
            "+YcWBRoXPm2l/nB4EbfdYwqNQe/HvpHE4vI0zdsY53vt5iCvYiRhKuk+ZtqN3Vw0\n" +
            "+d6e8KS5SfbltbKkH0zUaxQFNX9cVr5qDQfokKh3lNZ/fThQ0TMyTrI/vOffT38C\n" +
            "6k0QP/hEjjLTrXRoA2wDss+QmTw8dDkdesj234Lv0BUgEDywW6rkSt0/j/wAj1XB\n" +
            "cELQ4Y14XwQrR9TNF3K3NH5Nt05kFj1LncQ2rCh12kwHrfa/NAr7n+yn5e8vyAGw\n" +
            "7cGW\n" +
            "-----END CERTIFICATE-----";
}
