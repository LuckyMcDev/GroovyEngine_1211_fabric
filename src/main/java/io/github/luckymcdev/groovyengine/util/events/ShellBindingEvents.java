package io.github.luckymcdev.groovyengine.util.events;

import groovy.lang.Binding;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ShellBindingEvents {
    /**
     * Called after all default bindings are set but before shell creation
     */
    Event<BindingReady> BINDING_READY = EventFactory.createArrayBacked(BindingReady.class,
            (listeners) -> (binding) -> {
                for (BindingReady listener : listeners) {
                    listener.onBindingReady(binding);
                }
            });

    @FunctionalInterface
    interface BindingReady {
        void onBindingReady(Binding binding);
    }
}