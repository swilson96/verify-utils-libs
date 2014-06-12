package uk.gov.ida.common.shared.security;

import com.google.inject.Inject;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class PublicKeyFactory {

    private CertificateFactory certificateFactory;

    @Inject
    public PublicKeyFactory(CertificateFactory certificateFactory) {
        this.certificateFactory = certificateFactory;
    }

    public PublicKey createPublicKey(String partialCert) throws CertificateException {
        Certificate certificate = certificateFactory.createCertificate(partialCert);
        return certificate.getPublicKey();
    }
}
