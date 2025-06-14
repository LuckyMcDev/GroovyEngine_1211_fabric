package io.github.luckymcdev.groovyengine.editor.core;

import io.github.luckymcdev.groovyengine.editor.windows.BlockDisplayBrowserWindow;
import io.github.luckymcdev.groovyengine.editor.windows.ConsoleWindow;
import io.github.luckymcdev.groovyengine.editor.windows.DocsWindow;
import io.github.luckymcdev.groovyengine.editor.windows.EditorWindow;       // <-- added
import io.github.luckymcdev.groovyengine.editor.windows.ShaderWindow;
import io.github.luckymcdev.groovyengine.gemodelling.core.GeModellingImGui;
import io.github.luckymcdev.groovyengine.scripting.gui.GuiRegistry;
import io.github.luckymcdev.groovyengine.imgui.ImGuiImpl;
import imgui.ImGui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class MainEditorImGui {

    public static boolean showGroovyEngineModellingGroup = false;

    public static boolean showConsole = true;
    public static boolean showDocs = false;
    public static boolean showBlockBrowser = false;
    public static boolean showShaderEditor = false;
    public static boolean showEditorWindow = false; // new toggle for EditorWindow

    public static void render() {
        ImGuiImpl.draw(io -> {
            ImGui.pushFont(ImGuiImpl.getDefaultFont());

            renderMenuBar();

            GuiRegistry.renderAll();

            if (showGroovyEngineModellingGroup) {
                renderGroovyEngineModellingGroup();
            } else {
                if (showConsole) ConsoleWindow.render();
                if (showDocs) DocsWindow.render();
                if (showBlockBrowser) BlockDisplayBrowserWindow.render();
                if (showShaderEditor) ShaderWindow.render();
                if (showEditorWindow) EditorWindow.draw();
            }

            ImGui.popFont();
        });
    }

    private static void renderMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Windows")) {
                if (ImGui.menuItem("Console", "", showConsole)) showConsole = !showConsole;
                ImGui.separator();
                if (ImGui.menuItem("Script Editor", "", showEditorWindow)) showEditorWindow = !showEditorWindow;
                ImGui.separator();
                if (ImGui.menuItem("Documentation", "", showDocs)) showDocs = !showDocs;
                //if (ImGui.menuItem("Block Browser", "", showBlockBrowser)) showBlockBrowser = !showBlockBrowser;
                ImGui.separator();
                if (ImGui.menuItem("Shader Editor", "", showShaderEditor)) showShaderEditor = !showShaderEditor;
                ImGui.endMenu();
            }

            // New menu for Ge Modelling
            if (ImGui.beginMenu("GroovyEngine Modelling")) {
                if (ImGui.menuItem("Open GroovyEngine Modelling Screen")) {
                    // Close all other windows
                    showConsole = false;
                    showDocs = false;
                    showBlockBrowser = false;
                    showShaderEditor = false;
                    showEditorWindow = false;

                    // Open only modelling group
                    showGroovyEngineModellingGroup = true;
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Reset Layout")) {
                    showConsole = true;
                    showEditorWindow = true;   // reset EditorWindow visible on reset
                    showDocs = true;
                    showShaderEditor = true;
                }
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }

    private static void renderGroovyEngineModellingGroup() {
        GeModellingImGui.render();
    }

}
