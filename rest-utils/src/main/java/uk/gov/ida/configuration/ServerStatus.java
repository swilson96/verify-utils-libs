package uk.gov.ida.configuration;


public class ServerStatus {
    private static ServerStatus instance = null;
    protected ServerStatus(){

    }

    public static ServerStatus getInstance() {
        if(instance == null) {
            instance = new ServerStatus();
        }
        return instance;
    }
    private boolean serverStatus = false;

    public boolean isShutdownSequenceStarted() {return serverStatus; }

    public void setShutdownSequenceTo(boolean serverStatus) {
        this.serverStatus = serverStatus;
    }
}
