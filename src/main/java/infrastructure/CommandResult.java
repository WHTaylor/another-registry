package infrastructure;

import java.util.List;
import java.util.Optional;

public class CommandResult {
    private List<Event> events;
    private String failureMessage;

    public CommandResult (String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public CommandResult(List<Event> events) {
        this.events = events;
    }

    public static CommandResult failure(String s) {
        return new CommandResult(s);
    }

    public List<Event> getEvents() {
        return events;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public boolean wasSuccessful() {
        return events != null && events.size() > 0;
    }
}
