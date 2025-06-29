package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;
import imgui.type.ImBoolean;
import io.github.luckymcdev.groovyengine.scripting.builders.shaders.ShaderManager;

import java.util.Map;

public class ShaderWindow {
    private static final ImBoolean open = new ImBoolean(true);

    public static void render() {
        ImGui.begin("Shader Editor", open);

        ImGui.text("Available Shaders:");
        ImGui.separator();

        Map<String, ShaderManager.ShaderBuilder> shaders = ShaderManager.getShaders();

        for (Map.Entry<String, ShaderManager.ShaderBuilder> entry : shaders.entrySet()) {
            String shaderName = entry.getKey();
            ShaderManager.ShaderBuilder builder = entry.getValue();

            boolean enabled = builder.isEnabled();
            ImBoolean checkboxState = new ImBoolean(enabled);

            if (ImGui.checkbox(shaderName, checkboxState)) {
                if (checkboxState.get()) {
                    builder.enable();
                } else {
                    builder.disable();
                }
            }
        }

        ImGui.end();
    }
}
