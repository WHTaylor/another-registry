package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposerRemoved extends Event {
    private String un;

    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public ProposerRemoved() { super(); }

    public ProposerRemoved(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
