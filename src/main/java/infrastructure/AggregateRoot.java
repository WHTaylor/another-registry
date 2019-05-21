package infrastructure;

import java.util.UUID;

public abstract class AggregateRoot {
    private UUID id;
    private int eventsProcessed;

    protected AggregateRoot() {
        eventsProcessed = 0;
    }

    public int numEventsProcessed() {
        return eventsProcessed;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
