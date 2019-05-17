package infrastructure.exceptions;

public class CommandHandlerConflictException extends RuntimeException {
    public CommandHandlerConflictException(String message) {
        super(message);
    }
}
