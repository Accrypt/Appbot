package io.mzb.Appbot.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EventManager {

    private ArrayList<EventListener> listeners = new ArrayList<>();

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void callEvent(Event event) {
        ArrayList<Method> eventMethods = new ArrayList<>();
        for(EventListener listener : listeners) {
            for(Method method : listener.getClass().getMethods()) {
                if(method.isAnnotationPresent(EventHandler.class)) {
                    eventMethods.add(method);
                }
            }
        }
        for(Method method : eventMethods) {
            try {
                method.invoke(event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
