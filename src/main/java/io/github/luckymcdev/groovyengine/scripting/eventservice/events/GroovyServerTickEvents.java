package io.github.luckymcdev.groovyengine.scripting.eventservice.events;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GroovyServerTickEvents { // Renamed for clarity
    private static final List<Consumer<EventContext>> listeners = new ArrayList<>();

    public static void register(Consumer<EventContext> listener) {
        listeners.add(listener);
    }

    public static void fire(EventContext ctx) {
        for (Consumer<EventContext> l : listeners) {
            try {
                l.accept(ctx);
            } catch (Exception e) {
                GroovyEngine.LOGGER.error("Error firing ServerTick event to listener: {}", e.getMessage(), e);
            }
        }
    }

    public static void clear() {
        listeners.clear();
    }
}