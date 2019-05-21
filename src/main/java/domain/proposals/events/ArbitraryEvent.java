package domain.proposals.events;

import infrastructure.Event;

import java.util.UUID;

public class ArbitraryEvent extends Event {
    public ArbitraryEvent(UUID id) {
        super(id);
    }
}
