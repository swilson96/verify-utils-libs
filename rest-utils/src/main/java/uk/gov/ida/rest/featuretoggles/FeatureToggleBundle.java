package uk.gov.ida.rest.featuretoggles;

import com.google.common.base.Throwables;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.shared.utils.featuretoggles.FeatureRepository;

import java.lang.reflect.InvocationTargetException;

public class FeatureToggleBundle implements ConfiguredBundle<FeatureToggleConfiguration> {
    public FeatureToggleBundle() {
    }


    @Override
    public void initialize(Bootstrap <?> bootstrap) {
    }

    @Override
    public void run(
    final FeatureToggleConfiguration configuration,
    final Environment environment)
    throws IllegalAccessException, InvocationTargetException {

        if (configuration.getFeatureConfiguration() == null) {
            return;
        }

        FeatureRepository featureRepository = new FeatureRepository();
        try {
            featureRepository.loadFeatures(configuration.getFeatureConfiguration());
        }
        catch (ClassNotFoundException | NoSuchFieldException e) {
            Throwables.propagate(e);
        }
    }

}
