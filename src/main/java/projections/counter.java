package projections;

import domain.proposals.events.ProposalCreated;
import domain.proposals.events.ProposalSubmitted;
import infrastructure.EventHandler;

public class counter {
    private int numProposals;
    private int numSubmitted;

    public counter() {
        numProposals = 0;
        numSubmitted = 0;
    }

    public int getNumProposals() {
        return numProposals;
    }

    public int getNumSubmitted() {
        return numSubmitted;
    }

    @EventHandler
    public void on(ProposalCreated evt) {
        numProposals++;
    }

    @EventHandler
    public void on(ProposalSubmitted evt) {
        numSubmitted++;
    }

    public double getPercentageSubmitted() {
        return numProposals == 0 ? 100.0
                : numSubmitted == 0 ? 0.0
                : (float) numSubmitted / numProposals * 100;
    }
}
