package uk.gov.ida.docchecking.sharedapi.configuration;

public interface SSLContextConfiguration {


    public TrustedSslServersConfiguration getTrustedSslServers();

    public MutualAuthConfiguration getMutualAuth();
}
