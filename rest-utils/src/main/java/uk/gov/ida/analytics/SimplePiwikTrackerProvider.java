package uk.gov.ida.analytics;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.piwik.PiwikException;
import org.piwik.SimplePiwikTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.configuration.AnalyticsConfiguration;

import static java.text.MessageFormat.format;

public class SimplePiwikTrackerProvider implements Provider<SimplePiwikTracker> {

    private static final Logger LOG = LoggerFactory.getLogger(SimplePiwikTrackerProvider.class);

    private AnalyticsConfiguration analyticsConfiguration;

    @Inject
    public SimplePiwikTrackerProvider(AnalyticsConfiguration analyticsConfiguration){

        this.analyticsConfiguration = analyticsConfiguration;
    }

    @Override
    public SimplePiwikTracker get() {
        try {
            return new SimplePiwikTracker(format("http://{0}", analyticsConfiguration.getPiwikBaseUrl()));
        } catch (PiwikException e) {
            LOG.error("Piwik Provider error", e);
        }

        return null;
    }
}
