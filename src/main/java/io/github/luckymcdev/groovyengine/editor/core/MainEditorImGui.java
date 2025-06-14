package io.github.luckymcdev.groovyengine.editor.core;

import io.github.luckymcdev.groovyengine.editor.windows.BlockDisplayBrowserWindow;
import io.github.luckymcdev.groovyengine.editor.windows.ConsoleWindow;
import io.github.luckymcdev.groovyengine.editor.windows.DocsWindow;
import io.github.luckymcdev.groovyengine.editor.windows.ScriptBrowserWindow;
import io.github.luckymcdev.groovyengine.editor.windows.ShaderWindow;
import io.github.luckymcdev.groovyengine.scripting.gui.GuiRegistry;
import io.github.luckymcdev.groovyengine.imgui.ImGuiImpl;
import imgui.ImGui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MainEditorImGui {
    private static float menuBarHeight = 0; // This variable isn't used in the provided code
    // but keep it if you have plans for it.

    public static boolean showConsole = true;
    public static boolean showScripts = true;
    public static boolean showDocs = true;
    public static boolean showBlockBrowser = true;
    public static boolean showShaderEditor = false; // New: control visibility of ShaderWindow
    public static boolean showMinecraftView = false; // New: control visibility of MinecraftViewWindow

    public static void render() {
        ImGuiImpl.draw(io -> {
            ImGui.pushFont(ImGuiImpl.getDefaultFont());

            renderMenuBar();

            GuiRegistry.renderAll();

            if (showConsole) ConsoleWindow.render();
            if (showScripts) ScriptBrowserWindow.render();
            if (showDocs) DocsWindow.render();
            if (showBlockBrowser) BlockDisplayBrowserWindow.render();
            if (showShaderEditor) ShaderWindow.render(); // Render ShaderWindow if visible

            ImGui.popFont();
        });
    }

    private static void renderMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Windows")) {
                if (ImGui.menuItem("Console", "", showConsole)) showConsole = !showConsole;
                if (ImGui.menuItem("Script Browser", "", showScripts)) showScripts = !showScripts;
                if (ImGui.menuItem("Documentation", "", showDocs)) showDocs = !showDocs;
                if (ImGui.menuItem("Block Browser", "", showBlockBrowser)) showBlockBrowser = !showBlockBrowser;
                ImGui.separator(); // Separator for better organization
                if (ImGui.menuItem("Shader Editor", "", showShaderEditor)) showShaderEditor = !showShaderEditor; // Menu item for ShaderWindow
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Reset Layout")) {
                    showConsole = showScripts = showDocs = showBlockBrowser = showShaderEditor = showMinecraftView = true;
                }
                ImGui.endMenu();
            }

            if(ImGui.beginMenu("Shaders")) {
                if(ImGui.menuItem("Open Shader Editor", "", showShaderEditor)) {
                    showShaderEditor = !showShaderEditor;
                }
                // Removed "COMING SOON!" as we now have an editor
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }
    }
}