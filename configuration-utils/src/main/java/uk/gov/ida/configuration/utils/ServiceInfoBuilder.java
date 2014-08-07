package uk.gov.ida.configuration.utils;

public class ServiceInfoBuilder {

    private String name = "service name";

    public static ServiceInfoBuilder aServiceInfo() {
        return new ServiceInfoBuilder();
    }

    public ServiceInfo build() {
        return new TestServiceInfo(name);
    }

    public ServiceInfoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    private static class TestServiceInfo extends ServiceInfo {
        private TestServiceInfo(String name) {
            this.name = name;
        }
    }

}
