package domain.commands;

import infrastructure.Command;

import java.util.UUID;

public class CreateProposal extends Command {
    public CreateProposal(UUID id) {
        super(id);
    }
}
