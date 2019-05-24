package infrastructure;

import java.util.UUID;

public abstract class Event {
    private UUID aggregateId;

    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public Event() {
    }

    public Event(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }
}
