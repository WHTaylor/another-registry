import domain.Proposal;
import domain.commands.AddProposer;
import domain.commands.CreateProposal;
import domain.commands.SubmitProposal;
import infrastructure.*;
import projections.Counter;
import projections.ProposerStats;
import projections.SummaryView;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Class<? extends AggregateRoot>> aggregateRootClasses = Collections.singletonList(Proposal.class);
        Counter counter = new Counter();
        SummaryView view = new SummaryView();
        ProposerStats proposerStats = new ProposerStats();
        List<?> eventSubscribers = Arrays.asList(counter, view, proposerStats);
        MessageDispatcher dispatcher = setUp(aggregateRootClasses, eventSubscribers);

        for (int i = 0; i < 100000; i++) {
            UUID id = UUID.randomUUID();
            dispatcher.dispatch(new CreateProposal(id));
            if (i % 10000 == 0) {
                System.out.println(i);
            }
            if (Math.random() > 0.5) {
                dispatcher.dispatch(new SubmitProposal(id, "ref"));
            }

            while (Math.random() > 0.25) {
                dispatcher.dispatch(new AddProposer(id, Integer.toString((((int) (Math.random() * 10))))));
            }
        }

        System.out.println(counter.getNumProposals() + " proposals created, " +
                counter.getNumSubmitted() + " proposals submitted - " +
                counter.getPercentageSubmitted() + "% submitted");

        System.out.println(proposerStats);
    }

    private static MessageDispatcher setUp(Collection<Class<? extends AggregateRoot>> aggregateRootClasses,
                                           Collection<?> eventSubscribers) {
        EventStore eventStore = new InMemoryEventStore();

        AggregateRepo repo = new AggregateRepo(eventStore);
        repo.setUseCache(false);

        MessageDispatcher dispatcher = new MessageDispatcher(eventStore, repo);
        for (Class<? extends AggregateRoot> clazz : aggregateRootClasses) {
            dispatcher.registerAggregateRootClass(clazz);
        }

        for (Object subscriber : eventSubscribers) {
            dispatcher.registerEventSubscriber(subscriber);
        }

        return dispatcher;
    }
}
