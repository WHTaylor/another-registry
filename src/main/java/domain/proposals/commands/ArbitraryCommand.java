package domain.proposals.commands;

import infrastructure.Command;

import java.util.UUID;

public class ArbitraryCommand extends Command {
    public ArbitraryCommand(UUID id) {
        super(id);
    }
}
