package domain.proposals;

import domain.proposals.commands.CreateProposal;
import domain.proposals.commands.SubmitProposal;
import domain.proposals.events.ProposalCreated;
import domain.proposals.events.ProposalSubmitted;
import infrastructure.*;

import java.util.Collections;

public class Proposal extends AggregateRoot {
    private String referenceNumber;

    public Proposal() {
        super();
    }

    @CommandHandler
    public CommandResult handle(CreateProposal cmd) {
        if(getId() != null) {
            return CommandResult.failure("Proposal " + getId() + " already created");
        } else {
            return new CommandResult(Collections.singletonList(new ProposalCreated(cmd.getId())));
        }
    }

    @CommandHandler
    public CommandResult handle(SubmitProposal cmd) {
        if(referenceNumber != null) {
            return CommandResult.failure("Proposal " + getId() + " has already been submitted");
        } else {
            return new CommandResult(Collections.singletonList(new ProposalSubmitted(cmd.getId(), cmd.getReferenceNumber())));
        }
    }

    @EventApplier
    private void apply(ProposalCreated evt) {
        setId(evt.getId());
    }

    @EventApplier
    private void apply(ProposalSubmitted evt) {
        this.referenceNumber = referenceNumber;
    }
}
