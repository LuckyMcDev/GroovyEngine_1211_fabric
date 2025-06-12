package io.github.luckymcdev.api.scripting.gui;

import groovy.lang.Closure;

public class GuiBinding {
    public void register(String name, Closure<?> closure) {
        GuiRegistry.register(name, closure);
    }

    public void unregister(String name) {
        GuiRegistry.unregister(name);
    }
}
