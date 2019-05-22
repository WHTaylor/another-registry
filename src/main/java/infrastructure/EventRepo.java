package infrastructure;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventRepo {
    private EventStore eventStore;

    public EventRepo(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<Event> getEventsFor(UUID aggregateId) {
        return eventStore.getEventsForAggregate(aggregateId).stream()
                .map(Serializer::deserialize)
                .collect(Collectors.toList());
    }

    public void saveEvents(List<Event> events) {
        eventStore.saveEvents(events.stream()
                .map(Serializer::serialize)
                .collect(Collectors.toList()));
    }
}
