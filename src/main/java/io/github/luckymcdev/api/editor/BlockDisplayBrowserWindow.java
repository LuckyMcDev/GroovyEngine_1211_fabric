package io.github.luckymcdev.api.editor;

import imgui.ImGui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockDisplayBrowserWindow {
    public static void render() {
        ImGui.begin("Block Browser");
        ImGui.text("TODO: Implement block display browser...");
        ImGui.end();
    }
}
