package domain;

import domain.commands.AddProposer;
import domain.commands.AssignECRole;
import domain.commands.AssignPIRole;
import domain.commands.RemoveProposer;
import domain.events.ECRoleAssigned;
import domain.events.PIRoleAssigned;
import domain.events.ProposerAdded;
import domain.events.ProposerRemoved;
import infrastructure.CommandResult;
import infrastructure.Event;

import java.util.*;

class ProposalTeam {
    private List<String> proposerUns;
    private String pi;
    private String ec;

    ProposalTeam() {
        proposerUns = new ArrayList<>();
        pi = "";
        ec = "";
    }

    CommandResult handle(AddProposer cmd) {
        String un = cmd.getUn();
        if (proposerUns.size() >= 10) {
            return CommandResult.failure("Maximum 10 proposers on a proposal");
        } else if (proposerUns.contains(un)) {
            return CommandResult.failure("User " + un + " is already on proposal");
        }

        if (proposerUns.isEmpty()) {
            return new CommandResult(Arrays.asList(new ProposerAdded(cmd.getId(), un),
                    new PIRoleAssigned(cmd.getId(), un),
                    new ECRoleAssigned(cmd.getId(), un)));
        } else {
            return new CommandResult(Collections.singletonList(new ProposerAdded(cmd.getId(), un)));
        }
    }

    CommandResult handle(AssignPIRole cmd) {
        if (!proposerUns.contains(cmd.getUn())) {
            return CommandResult.failure("User " + cmd.getUn() + " is not on proposal, cannot assign as PI");
        }
        return new CommandResult(Collections.singletonList(new PIRoleAssigned(cmd.getId(), cmd.getUn())));
    }

    CommandResult handle(AssignECRole cmd) {
        if (!proposerUns.contains(cmd.getUn())) {
            return CommandResult.failure("User " + cmd.getUn() + " is not on proposal, cannot assign as EC");
        }
        return new CommandResult(Collections.singletonList(new ECRoleAssigned(cmd.getId(), cmd.getUn())));
    }

    CommandResult handle(RemoveProposer cmd) {
        String un = cmd.getUn();
        if (!proposerUns.contains(un)) {
            return CommandResult.failure("User " + un + " is not on proposal, cannot be removed");
        }

        List<Event> events = new ArrayList<>();
        events.add(new ProposerRemoved(cmd.getId(), un));

        if (un.equals(pi)) {
            Optional<String> replacementPi = proposerUns.stream().filter(v -> !v.equals(un)).findFirst();
            events.add(new PIRoleAssigned(cmd.getId(), replacementPi.orElse("")));
        }

        if (un.equals(ec)) {
            Optional<String> replacementEc = proposerUns.stream().filter(v -> !v.equals(un)).findFirst();
            events.add(new ECRoleAssigned(cmd.getId(), replacementEc.orElse("")));
        }

        return new CommandResult(events);
    }

    void apply(ProposerAdded evt) {
        proposerUns.add(evt.getUn());
    }

    void apply(PIRoleAssigned evt) {
        pi = evt.getUn();
    }

    void apply(ECRoleAssigned evt) {
        ec = evt.getUn();
    }

    void apply(ProposerRemoved evt) {
        proposerUns.remove(evt.getUn());
    }
}
