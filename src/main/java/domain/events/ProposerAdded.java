package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposerAdded extends Event {
    private String un;

    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public ProposerAdded() { super(); }

    public ProposerAdded(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
