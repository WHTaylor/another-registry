import domain.proposals.Proposal;
import domain.proposals.commands.ArbitraryCommand;
import domain.proposals.commands.CreateProposal;
import domain.proposals.commands.SubmitProposal;
import infrastructure.*;
import projections.Counter;
import projections.SummaryView;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        EventStore eventStore = new InMemoryEventStore();
        AggregateRepo repo = new AggregateRepo(eventStore);
        repo.registerAggregateRoot(Proposal.class);
        repo.setUseCache(false);
        MessageDispatcher dispatcher = new MessageDispatcher(eventStore, repo);
        dispatcher.registerCommandHandlers(Proposal.class);
        Counter counter = new Counter();
        SummaryView view = new SummaryView();
        dispatcher.registerEventSubscriber(counter);
        dispatcher.registerEventSubscriber(view);
        for (int i = 0; i < 5000000; i++) {
            UUID id = UUID.randomUUID();
            dispatcher.dispatch(new CreateProposal(id));
            if (i % 10000 == 0) {
                System.out.println(i);
            }
            if (Math.random() > 0.5) {
                dispatcher.dispatch(new SubmitProposal(id, "ref"));
            }

            while(Math.random() > 0.25) {
                dispatcher.dispatch(new ArbitraryCommand(id));
            }
        }

        System.out.println(counter.getNumProposals() + " proposals created, " +
                counter.getNumSubmitted() + " proposals submitted - " +
                counter.getPercentageSubmitted() + "% submitted");

        System.out.println("Average counter was " + view.getAverageCounter() + ", " +
                "proposal with highest counter was " + view.getSummaryWithHighestCount());
    }
}
