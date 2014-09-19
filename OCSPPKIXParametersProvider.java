package uk.gov.ida.shared.rest.config.verification;

import uk.gov.ida.truststore.IdaTrustStore;

import java.security.cert.PKIXParameters;

public class OCSPPKIXParametersProvider extends PKIXParametersProvider {
    @Override
    public PKIXParameters getPkixParameters(IdaTrustStore trustStore) {
        PKIXParameters pkixParameters = super.getPkixParameters(trustStore);
        pkixParameters.setRevocationEnabled(true);
        System.setProperty("ocspEnabled", "true");
        return pkixParameters;
    }
}
