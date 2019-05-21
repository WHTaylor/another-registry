package domain.proposals.events;

import infrastructure.Event;

import java.util.UUID;

public class PIRoleAssigned extends Event {
    private String un;

    public PIRoleAssigned(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
