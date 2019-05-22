package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposerAdded extends Event {
    private String un;

    public ProposerAdded(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
