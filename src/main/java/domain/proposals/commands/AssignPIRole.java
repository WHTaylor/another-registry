package domain.proposals.commands;

import infrastructure.Command;

import java.util.UUID;

public class AssignPIRole extends Command {
    private String un;

    public AssignPIRole(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
