package io.github.luckymcdev.api.editor;

import imgui.ImGui;

public class DocsWindow {
    public static void render() {
        ImGui.begin("GroovyEngine Docs");

        ImGui.textColored(0.9f, 0.9f, 0.2f, 1.0f, "Bindings & Helpers:");
        ImGui.bulletText("Events.on(name, callback) - Register to an event like 'blockBreak'");
        ImGui.bulletText("register(type, id, object) - Register items/blocks");
        ImGui.bulletText("create(Class, ...args) - Create an instance of a class");

        ImGui.separator();
        ImGui.text("Available bindings:");
        ImGui.bulletText("Logger - Log output from your script");
        ImGui.bulletText("ItemRegistryHelper - Register items");
        ImGui.bulletText("BlockRegistryHelper - Register blocks");
        ImGui.bulletText("Item, Block, Identifier - Common Minecraft classes");
        ImGui.bulletText("BlockSettings, FabricBlockSettings - Helpers for blocks");

        ImGui.separator();
        ImGui.textColored(0.7f, 0.8f, 1.0f, 1.0f, "Event Names:");
        ImGui.bulletText("clientTick");
        ImGui.bulletText("serverTick");
        ImGui.bulletText("blockBreak");
        ImGui.bulletText("blockPlace:<id>");
        ImGui.bulletText("playerJoin");
        ImGui.bulletText("playerLeave");

        ImGui.end();
    }
}
