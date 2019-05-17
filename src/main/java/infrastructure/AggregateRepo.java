package infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AggregateRepo {
    private HashMap<UUID, AggregateRoot> aggregateCache;
    private EventStore eventStore;
    private boolean useCache;

    public AggregateRepo(EventStore eventStore) {
        aggregateCache = new HashMap<>();
        this.eventStore = eventStore;
    }

    public void setUseCache(boolean b) {
        useCache = b;
    }

    AggregateRoot getAggregate(UUID id, Class<? extends AggregateRoot> clazz) {
        AggregateRoot agg = null;
        if (useCache) {
            try {
                agg = aggregateCache.getOrDefault(id, clazz.newInstance());
            } catch (Exception ex) {
                //TODO: Handle
                throw new RuntimeException("Creating new instance failed, crashing everything");
            }

            if (agg == null) {
                throw new RuntimeException("Very surprised if we can get here");
            } else {
                List<Event> events = eventStore.getEventsForAggregate(id);
                if (events.size() != agg.numEventsProcessed()) {
                    agg.applyEvents(events.subList(agg.numEventsProcessed(), events.size()));
                }
                aggregateCache.put(id, agg);
                return agg;
            }
        } else {
            try {
                agg = clazz.newInstance();
                List<Event> events = eventStore.getEventsForAggregate(id);
                agg.applyEvents(events);
                return agg;
            } catch (Exception ex) {
                throw new RuntimeException("Checked exceptions uhhh");
            }
        }
    }
}
