package projections;

import domain.proposals.events.ECRoleAssigned;
import domain.proposals.events.PIRoleAssigned;
import domain.proposals.events.ProposerAdded;
import domain.proposals.events.ProposerRemoved;
import infrastructure.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ProposerStats {
    private Map<UUID, ProposalTeam> proposalTeams;

    public ProposerStats() {
        proposalTeams = new HashMap<>();
    }

    @EventHandler
    public void on(ProposerAdded evt) {
        ProposalTeam team = proposalTeams.getOrDefault(evt.getId(), new ProposalTeam());
        team.uns.add(evt.getUn());
        proposalTeams.put(evt.getId(), team);
    }

    @EventHandler
    public void on(PIRoleAssigned evt) {
        ProposalTeam team = proposalTeams.get(evt.getId());
        team.pi = evt.getUn();
    }

    @EventHandler
    public void on(ECRoleAssigned evt) {
        ProposalTeam team = proposalTeams.get(evt.getId());
        team.ec = evt.getUn();
    }

    @EventHandler
    public void on(ProposerRemoved evt) {
        //Purposely not handling reshuffling pi/ec on removal to see if it matters - it should
        ProposalTeam team = proposalTeams.get(evt.getId());
        team.uns.remove(evt.getUn());
    }

    @Override
    public String toString() {
        Map<String, Long> unCounts = proposalTeams.values().stream()
                .map(t -> t.uns)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

        Map<String, Integer> piMap = proposalTeams.values().stream()
                .collect(Collectors.toMap(t -> t.pi, v -> 1, (v1, v2) -> v1 + 1));
        Map<String, Integer> ecMap = proposalTeams.values().stream()
                .collect(Collectors.toMap(t -> t.ec, v -> 1, (v1, v2) -> v1 + 1));

        StringBuilder sb = new StringBuilder("Proposal team stats (un=occurrences):\n");
        unCounts.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .ifPresent(v -> sb.append("Most common proposer: ").append(v).append("\n"));
        piMap.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .ifPresent(v -> sb.append("Most common pi: ").append(v).append("\n"));
        ecMap.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .ifPresent(v -> sb.append("Most common ec: ").append(v).append("\n"));

        return sb.toString();
    }

    private class ProposalTeam {
        List<String> uns;
        String pi;
        String ec;

        ProposalTeam() {
            uns = new ArrayList<>();
        }
    }
}
