package uk.gov.ida.shared.rest.config.verification;

import uk.gov.ida.shared.rest.exceptions.CertificateChainValidationException;
import uk.gov.ida.truststore.IdaTrustStore;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.cert.PKIXParameters;

public class PKIXParametersProvider {

    public PKIXParameters getPkixParameters(IdaTrustStore trustStore) {
        PKIXParameters certPathParameters;

        try {
            certPathParameters = new PKIXParameters(trustStore.getKeyStore().get());
        } catch (KeyStoreException | InvalidAlgorithmParameterException e) {
            throw new CertificateChainValidationException("There was an error reading from the trust store.", e);
        }
        certPathParameters.setRevocationEnabled(false);
        return certPathParameters;
    }
}
