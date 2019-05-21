package domain.proposals;

import domain.proposals.commands.ArbitraryCommand;
import domain.proposals.commands.CreateProposal;
import domain.proposals.commands.SubmitProposal;
import domain.proposals.events.ArbitraryEvent;
import domain.proposals.events.ProposalCreated;
import domain.proposals.events.ProposalSubmitted;
import infrastructure.*;

import java.util.Collections;

public class Proposal extends AggregateRoot {
    private String referenceNumber;
    private int counter;

    public Proposal() {
        super();
        counter = 0;
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

    @CommandHandler
    public CommandResult handle(ArbitraryCommand cmd) {
        return new CommandResult(Collections.singletonList(new ArbitraryEvent(cmd.getId())));
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
    public void apply(ArbitraryEvent evt) {
        this.counter += 1;
    }
}
