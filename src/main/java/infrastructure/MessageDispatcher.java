package infrastructure;

import infrastructure.exceptions.CommandHandlerConflictException;

import java.lang.reflect.InvocationTargetException;
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
    private EventRepo eventRepo;
    private AggregateRepo aggregateRepo;

    public MessageDispatcher(EventRepo eventRepo, AggregateRepo aggregateRepo) {
        this.eventRepo= eventRepo;
        this.aggregateRepo = aggregateRepo;
        commandHandlers = new HashMap<>();
        eventHandlers = new HashMap<>();
    }

    public void dispatch(Command cmd) {
        CommandResult result = commandHandlers.get(cmd.getClass()).apply(cmd);
        if (result.wasSuccessful()) {
            // Any way to make this transactional?
            eventRepo.saveEvents(result.getEvents());
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

    public void registerAggregateRootClass(Class<? extends AggregateRoot> aggregateClass) {
        for (Method method : aggregateClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandHandler.class)) {
                addCommandHandler(aggregateClass, method);
            }
        }

        aggregateRepo.registerAggregateRoot(aggregateClass);
    }

    public void registerEventSubscriber(Object subscriber) {
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                addEventHandler(subscriber, method);
            }
        }
    }

    // This is the kind of reflection magic needed
    // Assumes command handling methods must be on aggregate roots, would be good to change
    private void addCommandHandler(Class<? extends AggregateRoot> clazz, Method cmdHandler) {
        Class cmdType = cmdHandler.getParameterTypes()[0];
        if (commandHandlers.containsKey(cmdType)) {
            throw new CommandHandlerConflictException("Tried to register more than one command handler for " + cmdType);
        }

        commandHandlers.put(cmdType, cmd -> {
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
            } catch (IllegalAccessException | InvocationTargetException ex) {
                //TODO: Log, eventually some kind of retry queue?
                throw new RuntimeException("Event handler " + obj + " failed to handle event " + evt, ex);
            }
        };
        eventHandlers.computeIfAbsent(evtHandler.getParameterTypes()[0], k -> new ArrayList<>()).add(subscriber);
    }
}
