package infrastructure;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvent(Event evt);
    void saveEvents(List<Event> evts);
    List<Event> getEventsForAggregate(UUID id);
}
