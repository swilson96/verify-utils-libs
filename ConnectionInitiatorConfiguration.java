package uk.gov.ida.docchecking.sharedapi.configuration;

public interface ConnectionInitiatorConfiguration {


    public TrustedServerConfiguration getTrustedServers();

    public MutualAuthConfiguration getMutualAuth();
}
