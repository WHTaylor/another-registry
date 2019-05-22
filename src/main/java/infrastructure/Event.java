package infrastructure;

import java.util.UUID;

public abstract class Event {
    private UUID aggregateId;

    public Event(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }
}
