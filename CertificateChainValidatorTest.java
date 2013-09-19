package uk.gov.ida.shared.rest.config.verification;

import com.google.common.base.Throwables;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.shared.rest.truststore.DependentServiceSSLTrustStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateChainValidatorTest {

    public CertificateFactory x509Factory;
    public CertificateChainValidator certificateChainValidator;

    @Before
    public void setUp() throws Exception {
        x509Factory = CertificateFactory.getInstance("x509");
        DependentServiceSSLTrustStore trustStore = getTrustStore();
        certificateChainValidator = new CertificateChainValidator(trustStore);
    }

    @Test
    public void verify_shouldPassACertSignedByRootCACertInTrustStore() throws Exception {
        final X509Certificate intermediaryCACertificate = createX509Certificate(intermediaryCACertString);

        certificateChainValidator.validate(intermediaryCACertificate);
    }

    @Test
    public void verify_shouldPassACertSignedByAnIntermediaryCACertSignedByRootCACertInTrustStore() throws Exception {
        final X509Certificate encryptionCertificate = createX509Certificate(this.encryptionCertString);

        certificateChainValidator.validate(encryptionCertificate);
    }

    @Test(expected = CertPathValidatorException.class)
    public void verify_shouldFailACertSignedByAnUnknownRootCACert() throws Exception {
        final X509Certificate otherChildCertificate = createX509Certificate(childSignedByOtherRootCAString);

        certificateChainValidator.validate(otherChildCertificate);
    }

    private X509Certificate createX509Certificate(String certificateString) throws CertificateException, UnsupportedEncodingException {
        return (X509Certificate)
                x509Factory.generateCertificate(new ByteArrayInputStream(certificateString.getBytes("UTF-8")));
    }

    public DependentServiceSSLTrustStore getTrustStore() {
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
        return new DependentServiceSSLTrustStore(ks);
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

    public String otherRootCACertString = "-----BEGIN CERTIFICATE-----\n" +
            "MIIEjDCCA3SgAwIBAgIJAN5dEk++usvXMA0GCSqGSIb3DQEBBQUAMIGKMQswCQYD\n" +
            "VQQGEwJHQjEPMA0GA1UECBMGTG9uZG9uMQ8wDQYDVQQHEwZMb25kb24xFzAVBgNV\n" +
            "BAoTDkNhYmluZXQgT2ZmaWNlMQwwCgYDVQQLEwNHRFMxFTATBgNVBAMTDE1hcmsg\n" +
            "Um9vdCBDQTEbMBkGCSqGSIb3DQEJARYMbWFyay50YXlsb3IxMB4XDTEzMDkxODEz\n" +
            "MjAyNVoXDTE2MDcwODEzMjAyNVowgYoxCzAJBgNVBAYTAkdCMQ8wDQYDVQQIEwZM\n" +
            "b25kb24xDzANBgNVBAcTBkxvbmRvbjEXMBUGA1UEChMOQ2FiaW5ldCBPZmZpY2Ux\n" +
            "DDAKBgNVBAsTA0dEUzEVMBMGA1UEAxMMTWFyayBSb290IENBMRswGQYJKoZIhvcN\n" +
            "AQkBFgxtYXJrLnRheWxvcjEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB\n" +
            "AQDVFubvXrYy2aivRo5D+s1JhAMmj17bdfkY6znrpN8z1anPdV3rt4tdZGUnh+kM\n" +
            "ldF5Ndzxg/0vJz9Ht7qovBxQsDjP1X02nnigLa25kR3KJ1uLx2ec6rWex9mWN0ew\n" +
            "3XOoSlHmpEI62onu9l/Y3G09GRFnzWghVGs5nE+gsJCNfpx5AS6HK1FCwvfk5Hvn\n" +
            "JVz10g+yiA/FYtUvmQxNxlJemFgxgIHXipCQb1VfiFCwJc7VP3RfYggSTUdzJEZj\n" +
            "vjdOtf7BUm3U47zKzK+TACnb+4awRWOKDieWGgVA1fVtM2CBqxi8OOgcvLry1xcP\n" +
            "yEed4j0hlYjJKO0kQ9IB2Yf3AgMBAAGjgfIwge8wHQYDVR0OBBYEFA74ESKAj2X6\n" +
            "Umb4fR5JZk195FGYMIG/BgNVHSMEgbcwgbSAFA74ESKAj2X6Umb4fR5JZk195FGY\n" +
            "oYGQpIGNMIGKMQswCQYDVQQGEwJHQjEPMA0GA1UECBMGTG9uZG9uMQ8wDQYDVQQH\n" +
            "EwZMb25kb24xFzAVBgNVBAoTDkNhYmluZXQgT2ZmaWNlMQwwCgYDVQQLEwNHRFMx\n" +
            "FTATBgNVBAMTDE1hcmsgUm9vdCBDQTEbMBkGCSqGSIb3DQEJARYMbWFyay50YXls\n" +
            "b3IxggkA3l0ST766y9cwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEA\n" +
            "FN3wgfJQFPuWqzn3R/2OAS0ey7pH5I4w4c9Gz67U6arKQ7sn4V/NTh+nktuRVZJB\n" +
            "zk9AqhhLR3/rSw/gcH4Vx/xu4W0QwkJh5aviKfh+Z3Q2W1kikelbv7JnRAwt+lft\n" +
            "I89ShbB8Y0mvwsN+gMj2z7KUwt7CcLe+kMdaC5nX5210L7CVwnIcyA6XeXI2+zaw\n" +
            "pzVF8k+A1B578jb0ppfm4SLQX0uWM9Ndlxb31LsL7SeAcQhUjhjWYXGoYhpT20vO\n" +
            "vRQ0E9Je7xgJTH0OVTz5+gEYLJ0WdcKzQeWGryS7HCwH9XMWP/tfotyWCORNdFr9\n" +
            "x/VHS5Kns6lj7Kd0UPKv7Q==\n" +
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
