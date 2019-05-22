import domain.Proposal;
import domain.commands.CreateProposal;
import domain.commands.SubmitProposal;
import domain.events.ProposalCreated;
import domain.events.ProposalSubmitted;
import infrastructure.*;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProposalTest {

    @Test
    public void createProposal_newProposalInstance_successful() {
        Proposal proposal = new Proposal();

        CommandResult result = proposal.handle(new CreateProposal(UUID.randomUUID()));

        assertEquals("One event should be returned", 1, result.getEvents().size());
        assertEquals("Event should be 'ProposalCreated'", ProposalCreated.class, result.getEvents().get(0).getClass());
    }

    @Test
    public void createProposal_alreadyCreated_failure() {
        Proposal proposal = new Proposal();
        proposal.apply(new ProposalCreated(UUID.randomUUID()));

        CommandResult result = proposal.handle(new CreateProposal(UUID.randomUUID()));

        assertFalse("Command should fail", result.wasSuccessful());
    }

    @Test
    public void submitProposal_unsubmitted_successful() {
        Proposal proposal = new Proposal();
        UUID id = UUID.randomUUID();
        proposal.apply(new ProposalCreated(id));

        CommandResult result = proposal.handle(new SubmitProposal(id, "A reference number"));

        assertEquals("One event should be returned", 1, result.getEvents().size());
        assertEquals("Event should be 'ProposalCreated'", ProposalSubmitted.class, result.getEvents().get(0).getClass());
    }

    @Test
    public void submitProposal_alreadySubmitted_failure() {
        Proposal proposal = new Proposal();
        UUID id = UUID.randomUUID();
        proposal.apply(new ProposalCreated(id));
        proposal.apply(new ProposalSubmitted(id, "A reference number"));

        CommandResult result = proposal.handle(new SubmitProposal(id, "Another reference number"));

        assertFalse("Command should fail", result.wasSuccessful());
    }
}
