package uk.gov.ida.shared.utils.featuretoggles;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class FeatureConfiguration {
    @Valid
    @NotNull
    public List<FeatureEntry> features;

    @Valid
    @NotNull
    public String featureClass;

    public List<FeatureEntry> getFeatures() { return features; }
    public String getFeatureClass() { return featureClass; }
}
