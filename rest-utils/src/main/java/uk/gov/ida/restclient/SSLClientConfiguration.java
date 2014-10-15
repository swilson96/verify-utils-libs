package uk.gov.ida.restclient;

import org.apache.http.conn.scheme.SchemeRegistry;

import java.util.Map;

public class SSLClientConfiguration {

    private final SchemeRegistry schemeRegistry;
    private Map<String, Object> configurationProperties;

    public SSLClientConfiguration(SchemeRegistry schemeRegistry, Map<String, Object> configurationProperties){

        this.schemeRegistry = schemeRegistry;
        this.configurationProperties = configurationProperties;
    }

    public SchemeRegistry getSchemeRegistry() {
        return schemeRegistry;
    }

    public Map<String, Object> getConfigurationProperties() {
        return configurationProperties;
    }
}
