package uk.gov.ida.docchecking.sharedapi.configuration;

public interface SSLContextConfiguration {


    public TrustedServerConfiguration getTrustedServers();

    public MutualAuthConfiguration getMutualAuth();
}
