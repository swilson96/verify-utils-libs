package uk.gov.ida.configuration;


public class ServiceStatus {
    private static ServiceStatus instance = null;
    private boolean serverStatus = true;

    protected ServiceStatus(){

    }
    public static ServiceStatus getInstance() {
        if(instance == null) {
            instance = new ServiceStatus();
        }
        return instance;
    }

    public boolean isServerStatusOK() {return serverStatus; }

    public void setServiceStatus(boolean serverStatus) {
        this.serverStatus = serverStatus;
    }
}
