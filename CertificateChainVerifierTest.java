package uk.gov.ida.shared.rest.config.verification;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.google.common.collect.ImmutableList.of;

public class CertificateChainVerifierTest {

    public CertificateFactory x509Factory;
    public CertificateChainVerifier certificateChainVerifier;

    @Before
    public void setUp() throws Exception {
        x509Factory = CertificateFactory.getInstance("x509");
        certificateChainVerifier = new CertificateChainVerifier();
    }

    @Test
    public void verify_shouldPassACertSignedByRootCACert() throws Exception {
        final X509Certificate rootCACertificate = createX509Certificate(rootCACertString);
        final X509Certificate intermediaryCACertificate = createX509Certificate(intermediaryCACertString);

        certificateChainVerifier.verify(of(intermediaryCACertificate), rootCACertificate);
    }

    @Test
    public void verify_shouldPassACertSignedByAnIntermediarySignedByRootCACert() throws Exception {
        final X509Certificate rootCACertificate = createX509Certificate(rootCACertString);
        final X509Certificate intermediaryCACertificate = createX509Certificate(intermediaryCACertString);
        final X509Certificate encryptionCertificate = createX509Certificate(this.encryptionCertString);

        certificateChainVerifier.verify(of(encryptionCertificate, intermediaryCACertificate), rootCACertificate
        );
    }

    @Test(expected = CertPathValidatorException.class)
    public void verify_shouldFailACertSignedByAnUnknownRootCACert() throws Exception {
        final String certificateString = otherRootCACertString;
        final X509Certificate otherRootCACertificate = createX509Certificate(certificateString);
        final X509Certificate intermediaryCACertificate = createX509Certificate(intermediaryCACertString);

        certificateChainVerifier.verify(of(otherRootCACertificate), intermediaryCACertificate);
    }

    private X509Certificate createX509Certificate(String certificateString) throws CertificateException, UnsupportedEncodingException {
        return (X509Certificate)
                x509Factory.generateCertificate(new ByteArrayInputStream(certificateString.getBytes("UTF-8")));
    }

    private final String rootCACertString = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFbzCCA1egAwIBAgIQLSYxPZcb2dYMnb4BZHXvKTANBgkqhkiG9w0BAQsFADBQ\n" +
            "MQswCQYDVQQGEwJHQjEXMBUGA1UEChMOQ2FiaW5ldCBPZmZpY2UxDDAKBgNVBAsT\n" +
            "A0dEUzEaMBgGA1UEAxMRSURBUCBUZXN0IFJvb3QgQ0EwHhcNMTMwODAyMDAwMDAw\n" +
            "WhcNMjMwODAxMjM1OTU5WjBQMQswCQYDVQQGEwJHQjEXMBUGA1UEChMOQ2FiaW5l\n" +
            "dCBPZmZpY2UxDDAKBgNVBAsTA0dEUzEaMBgGA1UEAxMRSURBUCBUZXN0IFJvb3Qg\n" +
            "Q0EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDB0+tAA/QdnQYFKtaO\n" +
            "FkuRg23LesvKbS+qO4PF8EoTEVpyRlkAZVFCguUfsMVhX5UafDhvxqSRaUqBWX6Z\n" +
            "dwH1gtyPuTavQIKKpg1y8dLn4JklZCYLipg1PH1rnOmVIIlwPuRBK/Uqw5SzzSDh\n" +
            "keZyl26cmmuKNagUQ7pj6JQ3bVE/iMTz0SpaZCCTvmM0kbnfFDWohhnQZ/atdkzs\n" +
            "8PNNGe6axF5wC5uW++WMRS6T+dqCSBykp51f83QzhIfkZKBslXT1qooqjAiwFqPj\n" +
            "+IfP2FSrGjCkcoms+zv8QFG/skjRHI/zQU8np47xDb6aTrF0Z7QkJr8bAvrocyZW\n" +
            "4TJ4lqYPpZZBJIU+pC1H1MNHDg0q+kBcFLQpD01CbqBY865NzCJOJyeECiwomxDG\n" +
            "DH58PcILYFRW5D+azOb14QuQ1Yof7zFWQa4pgEypiXG6F6L62Jqd3V4fBRImyu8G\n" +
            "peJQDRKfBe2ymfxZJ3NBzGYEyNPvvOJIFEQ41wnCKa8Enq0LuuVyonOpxBjJPmtw\n" +
            "s3jpXDxQAiJ1wci2vxLA4RAz1Yy5OWfdtvznFjzNT0yeTX9EsV4ibxI5MM7wrOSJ\n" +
            "Ran69XdLN1uRZxQxoYEU3syAakZMrBfSJx7CRG1u0hGt8DSdVpcHKP0JkzTFnnui\n" +
            "CZgXzkxlBTBuwLjZDXNUT967cwIDAQABo0UwQzASBgNVHRMBAf8ECDAGAQH/AgEB\n" +
            "MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUY1DlEvzqocu7imr+k7WcwjGZRDYw\n" +
            "DQYJKoZIhvcNAQELBQADggIBACC3RN912iDQelRAwJXNgL0jUa809zxAWKou3lF/\n" +
            "1XkYuCTI84X2gRJ+KliLC/B1ehSr6eqjx4mvqjD02+uB+iudn1JXzabLbztEzXSq\n" +
            "NPtqCMjiwHx9i5oXDWaUl51FHbkWBZE38NKyg8TixRiF/uzFoa+q9A8I9GwZ6SAf\n" +
            "mPpxItPuwNbkpHj2RGUv3T2E9+8U0LGT3/LfeQhMdKbn1NLUbSNAynoGYaki9vvr\n" +
            "/5a1ah+H53wQinrSChT1UL2r/ANUwaTi/r4TRvFm4x15zXcre/8KNu462TZoosVD\n" +
            "7Hs5zfLPfwXzX8dCjadBph5Ixbke/L+2AzTQ9DdXGTU8qYD6EykZmz0U0+NFlDDV\n" +
            "M04jNW0CEf82ZkeD66hwNr3LRtEocrkokMRvtZzt+rLGyrHmn3WYklLaKNv4rSAk\n" +
            "DEKhe7KkMmFH6t6iAZS4rHyfCmmolz4z5MI4p794Gpg9hKW2NhhdtZOc74CWsTxE\n" +
            "GlAfpZ2wiMhC9jVYtr4jfuzXN9KEWUxAJy51C5xxno2EH6VyUncoRhKTTcNtREwp\n" +
            "uvgkA7Cu8yKYc4CD2tmo9nY9ji1yVPjKzpuC+xgUMcnXKRXVCBTSdwrR2ByGKH9T\n" +
            "ZqwbHYF8HZ/o4+pt48kj//RdsXdG0Q3ffWk5noy628g/9JKrDJt354bYC+YKYrO4\n" +
            "Uwtn\n" +
            "-----END CERTIFICATE-----";

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
}
