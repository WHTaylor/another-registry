package projections;

import domain.events.ProposalCreated;
import domain.events.ProposalSubmitted;
import infrastructure.EventHandler;

import java.util.*;

public class SummaryView {
    private Map<UUID, ProposalSummary> summaries;

    public SummaryView() {
        summaries = new HashMap<>();
    }

    @EventHandler
    public void on(ProposalCreated evt) {
        summaries.put(evt.getAggregateId(), new ProposalSummary(evt.getAggregateId()));
    }

    @EventHandler
    public void on(ProposalSubmitted evt) {
        ProposalSummary summary = summaries.get(evt.getAggregateId());
        summary.setReferenceNumber(evt.getReferenceNumber());
        summaries.put(evt.getAggregateId(), summary);
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

        ProposalSummary(UUID id) {
            this.id = id;
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

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        @Override
        public String toString() {
            return "Proposal " + id + ", " +
                    "reference " + referenceNumber + ", " +
                    "title " + title;
        }
    }
}
