package infrastructure;

import java.util.UUID;

public abstract class Command {
    private UUID id;

    public Command(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
