package uk.gov.ida.eventsink;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import uk.gov.ida.common.ServiceInfo;
import uk.gov.ida.common.SessionId;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class EventSinkMessageSender {

    private EventSinkProxy eventSinkProxy;
    private ServiceInfo serviceInfo;

    @Inject
    public EventSinkMessageSender(EventSinkProxy eventSinkProxy, ServiceInfo serviceInfo) {
        this.eventSinkProxy = eventSinkProxy;
        this.serviceInfo = serviceInfo;
    }

    public void audit(Exception exception, UUID errorId, SessionId sessionId, EventDetails... additionalDetails) {
        Map<EventDetailsKey, String> details = new HashMap<>(ImmutableMap.<EventDetailsKey, String>of(
                EventDetailsKey.error_id, errorId.toString(),
                EventDetailsKey.message, exception.getMessage())
        );

        for (EventDetails simpleEntry : additionalDetails) {
            details.put(simpleEntry.getKey(), simpleEntry.getValue());
        }

        EventSinkHubEvent event = new EventSinkHubEvent(this.serviceInfo, sessionId, EventSinkHubEventConstants.EventTypes.ERROR_EVENT, details);
        eventSinkProxy.logHubEvent(event);
    }
}
