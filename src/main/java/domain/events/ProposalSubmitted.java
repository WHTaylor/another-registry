package domain.events;

import infrastructure.Event;

import java.util.UUID;

public class ProposalSubmitted extends Event{
    private String referenceNumber;

    public ProposalSubmitted(UUID id, String referenceNumber) {
        super(id);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
