package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.gov.ida.shared.configuration.FeatureFlagConfiguration;
import uk.gov.ida.shared.rest.truststore.IdaTrustStore;

public class CertificateChainValidatorProvider implements Provider<CertificateChainValidator> {

    private final FeatureFlagConfiguration featureFlagConfiguration;
    private final IdaTrustStore trustStore;

    @Inject
    public CertificateChainValidatorProvider(FeatureFlagConfiguration featureFlagConfiguration, IdaTrustStore trustStore) {
        this.featureFlagConfiguration = featureFlagConfiguration;
        this.trustStore = trustStore;
    }

    @Override
    public CertificateChainValidator get() {
        if (featureFlagConfiguration.isCertificateChainValidationRequired()) {
            return new CertificateChainValidator(trustStore);
        }
        return new NoOpCertificateChainValidator(trustStore);
    }
}
