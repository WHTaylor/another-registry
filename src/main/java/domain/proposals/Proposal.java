package domain.proposals;

import domain.proposals.commands.*;
import domain.proposals.events.*;
import infrastructure.*;

import java.util.Collections;

public class Proposal extends AggregateRoot {
    private String referenceNumber;
    private ProposalTeam proposers;

    public Proposal() {
        super();
        proposers = new ProposalTeam();
    }

    @CommandHandler
    public CommandResult handle(CreateProposal cmd) {
        if (getId() != null) {
            return CommandResult.failure("Proposal " + getId() + " already created");
        } else {
            return new CommandResult(Collections.singletonList(new ProposalCreated(cmd.getId())));
        }
    }

    @CommandHandler
    public CommandResult handle(SubmitProposal cmd) {
        if (referenceNumber != null) {
            return CommandResult.failure("Proposal " + getId() + " has already been submitted");
        } else {
            return new CommandResult(Collections.singletonList(new ProposalSubmitted(cmd.getId(), cmd.getReferenceNumber())));
        }
    }

    @CommandHandler
    public CommandResult handle(AddProposer cmd) {
        return proposers.handle(cmd);
    }

    @CommandHandler
    public CommandResult handle(AssignPIRole cmd) {
        return proposers.handle(cmd);
    }

    @CommandHandler
    public CommandResult handle(AssignECRole cmd) {
        return proposers.handle(cmd);
    }

    @CommandHandler
    public CommandResult handle(RemoveProposer cmd) {
        return proposers.handle(cmd);
    }

    @EventApplier
    public void apply(ProposalCreated evt) {
        setId(evt.getId());
    }

    @EventApplier
    public void apply(ProposalSubmitted evt) {
        this.referenceNumber = evt.getReferenceNumber();
    }

    @EventApplier
    public void apply(ProposerAdded evt) {
        proposers.apply(evt);
    }

    @EventApplier
    public void apply(PIRoleAssigned evt) {
        proposers.apply(evt);
    }

    @EventApplier
    public void apply(ECRoleAssigned evt) {
        proposers.apply(evt);
    }

    @EventApplier
    public void apply(ProposerRemoved evt) {
        proposers.apply(evt);
    }
}
