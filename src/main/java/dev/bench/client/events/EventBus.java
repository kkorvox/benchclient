package dev.bench.client.events;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBus {

    private final Map<Class<?>, List<Listener>> listeners = new ConcurrentHashMap<>();

    public void subscribe(Object obj) {
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(EventHandler.class)) continue;
            if (m.getParameterCount() != 1) continue;
            Class<?> type = m.getParameterTypes()[0];
            m.setAccessible(true);
            listeners.computeIfAbsent(type, k -> Collections.synchronizedList(new ArrayList<>()))
                     .add(new Listener(obj, m));
        }
    }

    public void unsubscribe(Object obj) {
        listeners.values().forEach(list -> list.removeIf(l -> l.owner() == obj));
    }

    public <E> void post(E event) {
        List<Listener> list = listeners.get(event.getClass());
        if (list == null) return;
        for (Listener l : list) {
            try { l.method().invoke(l.owner(), event); }
            catch (InvocationTargetException | IllegalAccessException e) {
                dev.bench.client.core.Client.LOG.error("EventBus error", e);
            }
        }
    }

    private record Listener(Object owner, Method method) {}
}
