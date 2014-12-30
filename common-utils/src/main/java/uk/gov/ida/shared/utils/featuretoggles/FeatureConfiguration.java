package uk.gov.ida.shared.utils.featuretoggles;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.text.MessageFormat.format;

public class FeatureConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureConfiguration.class);

    @Valid
    // It is valid for us to have no features i.e. a null/empty list
    public List<FeatureEntry> features;

    @Valid
    @NotNull
    public String featureClass;

    public boolean isFeatureActive(String feature) {
        for(FeatureEntry entry : features) {
            if(entry.getFeatureName().equals(feature)) {
                return entry.isActive();
            }
        }
        LOG.error(format("Attempted to use unknown feature flag: {0}", feature));
        return false;
    }

    public int getFeatureCount() {
        if(features==null) return 0;
        return features.size();
    }

    public List<FeatureEntry> getFeatures() {
        if(features!=null) return features;
        return ImmutableList.of();
    }

    public String getFeatureClass() { return featureClass; }
}
