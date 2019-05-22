package infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InMemoryEventStore implements EventStore {
    private HashMap<String, List<SerializedEvent>> events;

    public InMemoryEventStore() {
        events = new HashMap<>();
    }

    @Override
    public void saveEvents(List<SerializedEvent> evts) {
        if(evts.stream().map(SerializedEvent::getAggregateId).distinct().count() > 1) {
            throw new RuntimeException("When saving multiple events, must come from same aggregate");
        }
        events.computeIfAbsent(evts.get(0).getAggregateId(), k -> new ArrayList<>()).addAll(evts);
    }

    @Override
    public List<SerializedEvent> getEventsForAggregate(UUID id) {
        return events.getOrDefault(id, new ArrayList<>());
    }
}
