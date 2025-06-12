package io.github.luckymcdev.api.editor;

import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.api.scripting.gui.GuiRegistry;
import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;

public class MainEditorImGui {
    private static float menuBarHeight = 0;

    public static boolean showConsole = true;
    public static boolean showScripts = true;
    public static boolean showDocs = true;
    public static boolean showBlockBrowser = true;

    public static void render() {
        if (EditorState.open) {
            MinecraftClient.getInstance().mouse.unlockCursor();
        } else {
            MinecraftClient.getInstance().mouse.lockCursor();
        }

        ImGuiImpl.draw(io -> {
            renderMenuBar();

            GuiRegistry.renderAll();

            if (showConsole) ConsoleWindow.render();
            if (showScripts) ScriptBrowserWindow.render();
            if (showDocs) DocsWindow.render();
            if (showBlockBrowser) BlockDisplayBrowserWindow.render();
        });
    }

    private static void renderMenuBar() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), 0);

        int flags = ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.MenuBar;

        ImGui.begin("##MainMenuBar", flags);
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Windows")) {
                if (ImGui.menuItem("Console", "", showConsole)) showConsole = !showConsole;
                if (ImGui.menuItem("Script Browser", "", showScripts)) showScripts = !showScripts;
                if (ImGui.menuItem("Documentation", "", showDocs)) showDocs = !showDocs;
                if (ImGui.menuItem("Block Browser", "", showBlockBrowser)) showBlockBrowser = !showBlockBrowser;
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("View")) {
                if (ImGui.menuItem("Reset Layout")) {
                    showConsole = showScripts = showDocs = showBlockBrowser = true;
                }
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }
        menuBarHeight = ImGui.getWindowHeight();
        ImGui.end();
    }
}
