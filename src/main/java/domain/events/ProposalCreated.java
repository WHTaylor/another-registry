package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposalCreated extends Event {
    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public ProposalCreated() { super(); }

    public ProposalCreated(UUID id) {
        super(id);
    }
}
