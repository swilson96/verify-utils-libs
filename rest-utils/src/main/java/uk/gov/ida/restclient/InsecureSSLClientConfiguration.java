package uk.gov.ida.restclient;

import org.apache.http.conn.scheme.SchemeRegistry;

import java.util.Map;

public class InsecureSSLClientConfiguration {

    private final SchemeRegistry schemeRegistry;
    private Map<String, Object> configurationProperties;

    public InsecureSSLClientConfiguration(SchemeRegistry schemeRegistry, Map<String, Object> configurationProperties){

        this.schemeRegistry = schemeRegistry;
        this.configurationProperties = configurationProperties;
    }

    public SchemeRegistry getSchemeRegistry() {
        return schemeRegistry;
    }

    public void addProperty(String key, Object value){
        this.configurationProperties.put(key, value);
    }

    public Map<String, Object> getConfigurationProperties() {
        return configurationProperties;
    }
}
