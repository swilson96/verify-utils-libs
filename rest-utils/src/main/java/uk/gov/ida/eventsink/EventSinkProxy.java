package uk.gov.ida.eventsink;

public interface EventSinkProxy {

    void logHubEvent(EventSinkHubEvent eventSinkHubEvent);
}
