package io.github.luckymcdev.groovyengine.gemodelling.core;

import imgui.ImGui;
import io.github.luckymcdev.groovyengine.editor.core.MainEditorImGui;

public class GeModellingImGui {
    public static void render() {
        ImGui.begin("GroovyEngine Modelling");

        ImGui.text("This is the GroovyEngine Modelling UI group.");

        // Add ImGuizmo or any modelling UI here!

        if (ImGui.button("Close Modelling")) {
            MainEditorImGui.showGroovyEngineModellingGroup = true;
        }

        ImGui.end();
    }
}
