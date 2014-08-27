package uk.gov.ida.configuration;

public class AnalyticsConfigurationBuilder {

    private boolean serverSideAnalyticsEnabled;

    public static AnalyticsConfigurationBuilder anAnalyticsConfiguration() {
        return new AnalyticsConfigurationBuilder();
    }


    public AnalyticsConfiguration build() {
        return new TestAnalyticsConfiguration(false, 9595, "http://piwik-digds.rhcloud.com/analytics", "http://analytics-1/analytics", serverSideAnalyticsEnabled);

    }

    public AnalyticsConfigurationBuilder withServerSideAnalyticsEnabled(boolean serverSideAnalyticsEnabled) {
        this.serverSideAnalyticsEnabled = serverSideAnalyticsEnabled;
        return this;
    }

    private static class TestAnalyticsConfiguration extends AnalyticsConfiguration {
        private TestAnalyticsConfiguration(boolean enabled, Integer siteId, String piwikBaseUrl, String piwikServerSideUrl, boolean serverSideAnalyticsEnabled) {
            this.enabled = enabled;
            this.siteId = siteId;
            this.piwikBaseUrl = piwikBaseUrl;
            this.piwikServerSideUrl = piwikServerSideUrl;
            this.serverSideAnalyticsEnabled = serverSideAnalyticsEnabled;
        }
    }
}
