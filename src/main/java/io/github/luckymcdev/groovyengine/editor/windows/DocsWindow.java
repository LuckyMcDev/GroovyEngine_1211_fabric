package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DocsWindow {
    public static void render() {

        ImGui.begin("GroovyEngine Scripting Docs");

        ImGui.text("Need someone dedicated to update this tbf");

        if (ImGui.treeNode("== Getting Started ==")) {
            ImGui.textWrapped("GroovyEngine lets you script Minecraft behavior using Groovy.");
            ImGui.textWrapped("Scripts are loaded from the '.minecraft/GroovyEngine' directory.");
            ImGui.textWrapped("Use powerful helpers to register items, blocks, events, and more.");
            ImGui.treePop();
        }

        ImGui.end();
    }
}
