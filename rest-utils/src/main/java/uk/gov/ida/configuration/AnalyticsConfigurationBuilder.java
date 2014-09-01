package uk.gov.ida.configuration;

public class AnalyticsConfigurationBuilder {

    public AnalyticsConfiguration build() {
        return new TestAnalyticsConfiguration(true, 9595, "http://piwik-digds.rhcloud.com/analytics", "http://analytics-1/analytics");

    }

    private static class TestAnalyticsConfiguration extends AnalyticsConfiguration {
        private TestAnalyticsConfiguration(boolean enabled, Integer siteId, String piwikBaseUrl, String piwikServerSideUrl) {
            this.enabled = enabled;
            this.siteId = siteId;
            this.piwikBaseUrl = piwikBaseUrl;
            this.piwikServerSideUrl = piwikServerSideUrl;
        }
    }
}
