package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposalCreated extends Event {
    public ProposalCreated(UUID id) {
        super(id);
    }
}
