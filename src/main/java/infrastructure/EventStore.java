package infrastructure;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    void saveEvents(List<SerializedEvent> evts);
    List<SerializedEvent> getEventsForAggregate(UUID id);
}
