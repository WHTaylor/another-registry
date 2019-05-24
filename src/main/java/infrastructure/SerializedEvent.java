package infrastructure;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Type;
import java.util.UUID;

@Entity
public class SerializedEvent {
    @Id
    private String id;
    private UUID aggregateId;
    private String eventType;
    private Integer aggregateVersion;
    private Integer eventVersion;
    // private Some kind of timestamp
    private String payload;

    public SerializedEvent(UUID aggregateId, Type eventType, /*Integer aggregateVersion, Integer eventVersion,*/ String payload) {
        this.aggregateId = aggregateId;
        this.eventType = eventType.getTypeName();
        // this.aggregateVersion = aggregateVersion;
        // this.eventVersion = eventVersion;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public Integer getAggregateVersion() {
        return aggregateVersion;
    }

    public Integer getEventVersion() {
        return eventVersion;
    }

    public String getPayload() {
        return payload;
    }
}
