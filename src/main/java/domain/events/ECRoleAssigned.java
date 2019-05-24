package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ECRoleAssigned extends Event {
    private String un;

    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public ECRoleAssigned() { super(); }

    public ECRoleAssigned(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
