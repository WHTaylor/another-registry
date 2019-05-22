package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposerRemoved extends Event {
    private String un;

    public ProposerRemoved(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
