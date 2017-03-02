package io.mzb.Appbot.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventManager {

    // List of all active listeners
    private ArrayList<EventListener> listeners = new ArrayList<>();

    /**
     * Adds a new event listener
     * @param listener The event listener to be registered
     */
    public void addListener(EventListener listener) {
        System.out.println("[Event] Registered: " + listener.getClass().getName());
        listeners.add(listener);
    }

    /**
     * Removes an event listener
     * @param listener The event listener to be removed
     */
    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Calls an event of that type
     * @param event The event to be called
     */
    public void callEvent(Event event) {
        System.out.println("[Event] Calling: " + event.getClass().getSimpleName());

        // Search every event listener
        for (EventListener listener : listeners) {
            // Search each method in that listener
            for (Method method : listener.getClass().getMethods()) {
                // Check if the event annotation is present
                if (method.isAnnotationPresent(EventHandler.class)) {
                    try {
                        // Try and call that event listener with the event provided
                        System.out.println("[Event] Invoke: " + method.getName() + " in " + listener.getClass().getSimpleName());
                        method.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }
}
