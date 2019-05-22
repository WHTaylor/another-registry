package domain.commands;

import infrastructure.Command;

import java.util.UUID;

public class SubmitProposal extends Command {
    private String referenceNumber;

    public SubmitProposal(UUID id, String referenceNumber) {
        super(id);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
