package io.github.luckymcdev.groovyengine.script_event;

import java.util.*;
import java.util.function.Consumer;

public class Events {
    private static final Map<String, List<Consumer<EventContext>>> listeners = new HashMap<>();

    public static void on(String event, Consumer<EventContext> callback) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(callback);
    }

    public static void trigger(String event, EventContext ctx) {
        List<Consumer<EventContext>> handlers = listeners.get(event);
        if (handlers != null) {
            for (Consumer<EventContext> handler : handlers) {
                try {
                    handler.accept(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clear() {
        listeners.clear();
    }

}
