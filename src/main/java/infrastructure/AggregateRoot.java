package infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;

public abstract class AggregateRoot {
    private UUID id;
    private int eventsProcessed;

    protected AggregateRoot() {
        eventsProcessed = 0;
    }

    public int numEventsProcessed() {
        return eventsProcessed;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    // Every time we want to apply an event, iterate over all methods reflectively.
    // Surely there's a better way?
    private void applyEvent(Event evt) {
        boolean completed = false;
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventApplier.class)) {
                if (evt.getClass().equals(method.getParameterTypes()[0])) {
                    try {
                        method.setAccessible(true);
                        method.invoke(this, evt);
                        completed = true;
                        break;
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (!completed) {
            throw new RuntimeException(String.format("Type %s cannot apply events of type %s", getClass(), evt.getClass()));
        }
    }

    public void applyEvents(Collection<Event> evts) {
        for (Event evt : evts) {
            applyEvent(evt);
        }
    }
}
