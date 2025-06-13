package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DocsWindow {
    public static void render() {

        ImGui.begin("GroovyEngine Scripting Docs");

        if (ImGui.treeNode("== Getting Started ==")) {
            ImGui.textWrapped("GroovyEngine lets you script Minecraft behavior using Groovy.");
            ImGui.textWrapped("Scripts are loaded from the '.minecraft/GroovyEngine' directory.");
            ImGui.textWrapped("Use powerful helpers to register items, blocks, events, and more.");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Scripting API & Bindings --")) {
            ImGui.textColored(0.8f, 0.9f, 1f, 1f, "Core Scripting Functions:");

            if (ImGui.treeNode(" Events.on(eventName, callback)")) {
                ImGui.textWrapped("Register a callback for a specific event.");
                ImGui.separator();
                ImGui.textColored(0.7f, 1f, 0.7f, 1f, "Example:");
                ImGui.textWrapped("""
                    Events.on("blockBreak", { ctx ->
                        Logger.info("Block broken: " + ctx.block)
                    })
                """);
                ImGui.treePop();
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Builders --")) {
            if (ImGui.treeNode(" ItemBuilder(name)")) {
                ImGui.textWrapped("Registers an item with display name:");
                ImGui.textWrapped("""
                    ItemBuilder("banana")
                        .displayName("Banana")
                        .build()
                """);
                ImGui.textWrapped("The Texture is the default location inside the resourcepack located in the data folder");
                ImGui.treePop();
            }

            if (ImGui.treeNode(" BlockBuilder(name)")) {
                ImGui.textWrapped("Registers a block with display name:");
                ImGui.textWrapped("""
                    BlockBuilder("test_block")
                        .displayName("Test Block")
                        .build()
                """);
                ImGui.treePop();
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Available Globals --")) {
            ImGui.text("These objects are available to all scripts:");
            ImGui.bulletText("- Logger: log info, warn, or error");
            ImGui.bulletText("- Events: hook into game events");
            ImGui.bulletText("- ItemBuilder, BlockBuilder: build items and blocks easily");
            ImGui.bulletText("- Minecraft classes (e.g., Item, Block, Identifier)");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Event Names --")) {
            ImGui.text("Supported event hooks:");
            ImGui.bulletText("- clientTick");
            ImGui.bulletText("- serverTick");
            ImGui.bulletText("- playerJoin / playerLeave");
            ImGui.bulletText("- blockBreak");
            ImGui.bulletText("- blockPlace:<id>");
            ImGui.bulletText("- registerItem");
            ImGui.bulletText("- registerBlock !--currently broken--!");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Example Script --")) {
            ImGui.text("A complete example using Events and Logger:");
            ImGui.separator();
            ImGui.textWrapped("""
                Events.on("playerJoin", { ctx ->
                    Logger.info("Welcome " + ctx.player.name)
                })
            """);
            ImGui.treePop();
        }

        ImGui.end();
    }
}
