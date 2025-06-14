package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;
import imgui.type.ImBoolean; // Ensure this is imported if using ImBoolean in begin

public class ShaderWindow {
    public static void render() {
        // Using an ImBoolean to allow the user to close the window
        ImGui.begin("Shader Editor", new ImBoolean(true)); // Added ImBoolean for consistency and close button
        ImGui.text("This is the Shader Editor!");
        ImGui.text("You can use this to manage your rendering!");

        ImGui.separator(); // Add a separator for the new section

        // New "paragraph" to display Minecraft's output
        ImGui.text("Minecraft Output Preview:");
        ImGui.newLine(); // Add some vertical space


        ImGui.end();
    }
}