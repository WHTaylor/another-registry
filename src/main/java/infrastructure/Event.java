package infrastructure;

import java.util.UUID;

public abstract class Event {
    private UUID id;

    public Event(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
