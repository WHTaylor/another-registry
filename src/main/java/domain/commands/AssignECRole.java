package domain.commands;

import infrastructure.Command;

import java.util.UUID;

public class AssignECRole extends Command {
    private String un;

    public AssignECRole(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
