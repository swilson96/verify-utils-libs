package uk.gov.ida.shared.rest.config.verification;

import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.gov.ida.shared.configuration.FeatureFlagConfiguration;
import uk.gov.ida.shared.rest.common.transformers.CertificateDtoToX509CertificateTransformer;
import uk.gov.ida.shared.rest.truststore.IdaTrustStore;

public class CertificateChainValidatorProvider implements Provider<CertificateChainValidator> {

    private final FeatureFlagConfiguration featureFlagConfiguration;
    private final IdaTrustStore trustStore;
    private final CertificateDtoToX509CertificateTransformer certificateTransformer;

    @Inject
    public CertificateChainValidatorProvider(
            FeatureFlagConfiguration featureFlagConfiguration,
            IdaTrustStore trustStore,
            CertificateDtoToX509CertificateTransformer certificateTransformer) {

        this.featureFlagConfiguration = featureFlagConfiguration;
        this.trustStore = trustStore;
        this.certificateTransformer = certificateTransformer;
    }

    @Override
    public CertificateChainValidator get() {
        if (featureFlagConfiguration.isCertificateChainValidationRequired()) {
            return new CertificateChainValidator(trustStore, certificateTransformer);
        }
        return new NoOpCertificateChainValidator(trustStore, certificateTransformer);
    }
}
