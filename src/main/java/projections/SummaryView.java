package projections;

import domain.proposals.events.ArbitraryEvent;
import domain.proposals.events.ProposalCreated;
import domain.proposals.events.ProposalSubmitted;
import infrastructure.EventHandler;

import java.util.*;

public class SummaryView {
    private Map<UUID, ProposalSummary> summaries;

    public SummaryView() {
        summaries = new HashMap<>();
    }

    @EventHandler
    public void on(ProposalCreated evt) {
        summaries.put(evt.getId(), new ProposalSummary(evt.getId()));
    }

    @EventHandler
    public void on(ProposalSubmitted evt) {
        ProposalSummary summary = summaries.get(evt.getId());
        summary.setReferenceNumber(evt.getReferenceNumber());
        summaries.put(evt.getId(), summary);
    }

    @EventHandler
    public void on(ArbitraryEvent evt) {
        summaries.get(evt.getId()).incCounter();
    }

    public ProposalSummary getSummaryWithHighestCount() {
        return summaries.values().stream().max(Comparator.comparingInt(ProposalSummary::getCounter)).get();
    }

    public double getAverageCounter() {
        return summaries.values().stream().mapToInt(ProposalSummary::getCounter).sum() / (float)summaries.values().size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ProposalSummary summary : summaries.values()) {
            sb.append(summary.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private class ProposalSummary {
        private UUID id;
        private String title;
        private String referenceNumber;
        private int counter;

        ProposalSummary(UUID id) {
            this.id = id;
            counter = 0;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public int getCounter() {
            return counter;
        }

        public void incCounter() {
            counter++;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        @Override
        public String toString() {
            return "Proposal " + id + ", " +
                    "reference " + referenceNumber + ", " +
                    "title " + title + ", " +
                    "counter " + counter;
        }
    }
}
