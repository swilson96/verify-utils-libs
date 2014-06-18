package uk.gov.ida.saml;

import java.net.URI;

public class SamlConfigurationBuilder {
    private String entityId = "entity-id";
    private URI expectedDestinationHost = URI.create("http://localhost");

    public static SamlConfigurationBuilder aSamlConfiguration() {
        return new SamlConfigurationBuilder();
    }

    public SamlConfigurationImpl build() {
        return new TestSamlConfiguration(entityId, expectedDestinationHost);
    }

    public SamlConfigurationBuilder withIssuerId(String issuerId) {
        this.entityId = issuerId;
        return this;
    }

    private static class TestSamlConfiguration extends SamlConfigurationImpl {
        private TestSamlConfiguration(String issuerId, URI expectedDestination) {
            this.entityId = issuerId;
            this.expectedDestination = expectedDestination;
        }
    }
}
