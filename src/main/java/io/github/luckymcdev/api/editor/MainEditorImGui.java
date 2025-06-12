package io.github.luckymcdev.api.editor;

import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.api.scripting.gui.GuiRegistry;
import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class MainEditorImGui {
    private static float menuBarHeight = 0;

    public static boolean showConsole = true;
    public static boolean showScripts = true;
    public static boolean showDocs = true;
    public static boolean showBlockBrowser = true;

    public static void render() {
        ImGuiImpl.draw(io -> {
            ImGui.pushFont(ImGuiImpl.getDefaultFont());

            renderDockSpace();

            renderMenuBar();

            GuiRegistry.renderAll();

            if (showConsole) ConsoleWindow.render();
            if (showScripts) ScriptBrowserWindow.render();
            if (showDocs) DocsWindow.render();
            if (showBlockBrowser) BlockDisplayBrowserWindow.render();

            ImGui.popFont();
        });
    }

    private static void renderDockSpace() {

        Window window = MinecraftClient.getInstance().getWindow();

        int flags = ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.MenuBar |
                ImGuiWindowFlags.NoBackground;

        ImGui.setNextWindowPos(0,0);
        ImGui.setNextWindowSize(window.getWidth(), window.getHeight());
        ImGui.begin("DOCKSPACE", flags);

        // Dockspace ID
        int dockspaceId = ImGui.getID("GroovyEngineDockspace");

        ImGui.dockSpace(dockspaceId, 0.0f, 0.0f, ImGuiDockNodeFlags.PassthruCentralNode);

        ImGui.end();
    }

    private static void renderMenuBar() {
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
