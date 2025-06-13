package io.github.luckymcdev.groovyengine.scripting.gui;

import groovy.lang.Closure;
import imgui.ImGui;

import java.util.LinkedHashMap;
import java.util.Map;

public class GuiRegistry {

    private static final Map<String, Closure<?>> registeredGuis = new LinkedHashMap<>();

    public static void register(String name, Closure<?> closure) {
        registeredGuis.put(name, closure);
    }

    public static void unregister(String name) {
        registeredGuis.remove(name);
    }

    public static void renderAll() {
        for (Map.Entry<String, Closure<?>> entry : registeredGuis.entrySet()) {
            String windowName = entry.getKey();
            Closure<?> closure = entry.getValue();

            ImGui.begin(windowName);
            try {
                closure.call();
            } catch (Exception e) {
                ImGui.textColored(1f, 0.2f, 0.2f, 1f, "Error in GUI script: " + e.getMessage());
            }
            ImGui.end();
        }
    }
}
