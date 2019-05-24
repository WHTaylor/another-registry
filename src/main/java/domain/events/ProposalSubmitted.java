package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposalSubmitted extends Event{
    private String referenceNumber;

    /**
     * Event and all subclasses require a no-arg constructor for deserialization
     */
    public ProposalSubmitted() { super(); }

    public ProposalSubmitted(UUID id, String referenceNumber) {
        super(id);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
