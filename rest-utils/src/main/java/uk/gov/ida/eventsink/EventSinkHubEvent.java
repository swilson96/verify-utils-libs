package uk.gov.ida.eventsink;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import uk.gov.ida.common.ServiceInfo;
import uk.gov.ida.common.SessionId;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class EventSinkHubEvent {
    private UUID eventId;
    private DateTime timestamp = DateTime.now();
    private String originatingService;
    private SessionId sessionId;
    private String eventType;
    private EnumMap<EventDetailsKey, String> details;

    @SuppressWarnings("unused") //Needed by Jaxb.
    private EventSinkHubEvent() {
    }

    public EventSinkHubEvent(ServiceInfo serviceInfo, SessionId sessionId, String eventType, Map<EventDetailsKey, String> details) {
        this.eventId = UUID.randomUUID();
        this.originatingService = serviceInfo.getName();
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.details = Maps.newEnumMap(details);
    }

    public UUID getEventId() {
        return eventId;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public String getOriginatingService() {
        return originatingService;
    }

    public String getSessionId() {
        return sessionId.toString();
    }

    public String getEventType() {
        return eventType;
    }

    public EnumMap<EventDetailsKey, String> getDetails() {
        return details;
    }
}
