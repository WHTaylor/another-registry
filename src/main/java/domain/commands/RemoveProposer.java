package domain.commands;

import infrastructure.Command;

import java.util.UUID;

public class RemoveProposer extends Command {
    private String un;

    public RemoveProposer(UUID id, String un) {
        super(id);
        this.un = un;
    }

    public String getUn() {
        return un;
    }
}
