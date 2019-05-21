package infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AggregateRepo {
    private HashMap<UUID, AggregateRoot> aggregateCache;
    private EventStore eventStore;
    private boolean useCache;

    private Map<Class<? extends AggregateRoot>, Map<Class<?>, BiConsumer<AggregateRoot, Event>>> eventAppliers;

    public AggregateRepo(EventStore eventStore) {
        aggregateCache = new HashMap<>();
        eventAppliers = new HashMap<>();
        this.eventStore = eventStore;
    }

    public void registerAggregateRoot(Class<? extends AggregateRoot> aggRootClass) {
        eventAppliers.put(aggRootClass, new HashMap<>());

        for (Method method : aggRootClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventApplier.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                eventAppliers.get(aggRootClass).put(eventType, (aggInstance, evt) -> {
                    try {
                        method.setAccessible(true);
                        method.invoke(aggInstance, evt);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
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
                    applyEvents(agg, events.subList(agg.numEventsProcessed(), events.size()));
                }
                aggregateCache.put(id, agg);
                return agg;
            }
        } else {
            try {
                agg = clazz.newInstance();
                List<Event> events = eventStore.getEventsForAggregate(id);
                applyEvents(agg, events);
                return agg;
            } catch (Exception ex) {
                throw new RuntimeException("Checked exceptions uhhh");
            }
        }
    }

    private void applyEvents(AggregateRoot agg, List<Event> events) {
        Class<? extends AggregateRoot> aggClass = agg.getClass();
        for (Event evt : events) {
            BiConsumer<AggregateRoot, Event> evtApplier = eventAppliers.get(aggClass).get(evt.getClass());
            if (evtApplier == null) {
                throw new RuntimeException(String.format("Type %s cannot apply events of type %s", aggClass, evt.getClass()));
            } else {
                evtApplier.accept(agg, evt);
            }
        }
    }

    public void setUseCache(boolean b) {
        useCache = b;
    }
}
