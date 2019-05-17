package infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InMemoryEventStore implements EventStore {
    private HashMap<UUID, List<Event>> events;

    public InMemoryEventStore() {
        events = new HashMap<>();
    }

    @Override
    public void saveEvent(Event evt) {
        events.computeIfAbsent(evt.getId(), k -> new ArrayList<>()).add(evt);
    }

    @Override
    public void saveEvents(List<Event> evts) {
        if(evts.stream().map(Event::getId).distinct().count() > 1) {
            throw new RuntimeException("When saving multiple events, must come from same aggregate");
        }

        events.computeIfAbsent(evts.get(0).getId(), k -> new ArrayList<>()).addAll(evts);
    }

    @Override
    public List<Event> getEventsForAggregate(UUID id) {
        return events.getOrDefault(id, new ArrayList<>());
    }
}
