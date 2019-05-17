package infrastructure;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class MessageDispatcher {
    private HashMap<Class, Function<Command, CommandResult>> commandHandlers;
    private HashMap<Class, Collection<Consumer<Event>>> eventHandlers;
    private EventStore eventStore;
    private AggregateRepo aggregateRepo;

    public MessageDispatcher(EventStore eventStore, AggregateRepo aggregateRepo) {
        this.eventStore = eventStore;
        this.aggregateRepo = aggregateRepo;
        commandHandlers = new HashMap<>();
        eventHandlers = new HashMap<>();
    }

    public void dispatch(Command cmd) {
        CommandResult result = commandHandlers.get(cmd.getClass()).apply(cmd);
        if (result.wasSuccessful()) {
            // Any way to make this transactional?
            eventStore.saveEvents(result.getEvents());
            for (Event evt : result.getEvents()) {
                publishEvent(evt);
            }
        }
    }

    private void publishEvent(Event evt) {
        for (Consumer<Event> subscriber : eventHandlers.getOrDefault(evt.getClass(), Collections.emptyList())) {
            subscriber.accept(evt);
        }
    }

    public void registerCommandHandlers(Class<? extends AggregateRoot> aggregateClass) {
        for (Method method : aggregateClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandHandler.class)) {
                addCommandHandler(aggregateClass, method);
            }
        }
    }

    public void registerEventSubscribers(Object subscriber) {
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                addEventHandler(subscriber, method);
            }
        }
    }

    // This is the kind of reflection magic needed
    // Assumes command handling methods must be on aggregate roots, would be good to change
    private void addCommandHandler(Class<? extends AggregateRoot> clazz, Method cmdHandler) {
        commandHandlers.put(cmdHandler.getParameterTypes()[0], cmd -> {
            try {
                AggregateRoot agg = aggregateRepo.getAggregate(cmd.getId(), clazz);
                return (CommandResult) cmdHandler.invoke(agg, cmd);
            } catch (Exception ex) {
                //TODO: Think about how to handle properly, probably log and crash
                return new CommandResult("Failed due to internal server error");
            }
        });
    }

    private void addEventHandler(Object obj, Method evtHandler) {
        // Could have async as attribute of EventHandler annotation, then if async start in new thread?
        // No return value might make this quite doable, any async library (flux) worth using?
        Consumer<Event> subscriber = evt -> {
            try {
                evtHandler.invoke(obj, evt);
            } catch (Exception ex) {
                //TODO: Log, eventually some kind of retry queue?
                ex.printStackTrace();
            }
        };
        eventHandlers.computeIfAbsent(evtHandler.getParameterTypes()[0], k -> new ArrayList<>()).add(subscriber);
    }
}
