package io.mzb.Appbot.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventManager {

    private ArrayList<EventListener> listeners = new ArrayList<>();

    public void addListener(EventListener listener) {
        System.out.println("[Event] Registered: " + listener.getClass().getName());
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void callEvent(Event event) {
        System.out.println("[Event] Calling: " + event.getClass().getSimpleName());
        for (EventListener listener : listeners) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    try {
                        System.out.println("[Event] Invoke: " + method.getName() + " in " + listener.getClass().getSimpleName());
                        method.invoke(listener, event);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
