package uk.gov.ida.common;

// TODO: rename to ServiceInfoConfiguration
public class ServiceInfo {

    protected String name;

    @SuppressWarnings("unused") //Needed by JAXB
    protected ServiceInfo() {
    }

    public ServiceInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
