package uk.gov.ida.shared.utils.featuretoggles;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.shared.utils.featuretoggles.FeatureConfigurationBuilder.aFeatureConfiguration;
import static uk.gov.ida.shared.utils.featuretoggles.FeatureEntryBuilder.aFeatureEntry;
import static uk.gov.ida.shared.utils.featuretoggles.IdaFeatures.EncodeAssertions;
import static uk.gov.ida.shared.utils.featuretoggles.IdaFeatures.UIRework;

public class FeatureTest {
    @Test
    public void should_returnCreateDate() throws Exception {
        FeatureConfiguration configuration = aFeatureConfiguration()
                .withFeatureClass(IdaFeatures.class.getCanonicalName())
                .withFeature(aFeatureEntry().withFeatureName(UIRework.name()).isActive(true).build())
                .withFeature(aFeatureEntry().withFeatureName(EncodeAssertions.name()).isActive(false).build())
                .build();
        FeatureRepository featureRepository = new FeatureRepository();
        featureRepository.loadFeatures(configuration);

        assertThat(UIRework.getCreateDate()).isEqualTo(DateTime.parse("2014-08-14"));
    }
}
