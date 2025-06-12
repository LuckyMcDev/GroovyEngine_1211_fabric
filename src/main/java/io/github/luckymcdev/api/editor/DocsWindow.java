package io.github.luckymcdev.api.editor;

import imgui.ImGui;

public class DocsWindow {
    public static void render() {
        ImGui.begin("GroovyEngine Scripting Docs");

        if (ImGui.treeNode("== Getting Started ==")) {
            ImGui.textWrapped("GroovyEngine lets you script Minecraft behavior using Groovy.");
            ImGui.textWrapped("Scripts are loaded from the 'GroovyEngine' folder in your .minecraft directory.");
            ImGui.textWrapped("To react to events or create new blocks/items, you use the bindings listed below.");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Bindings & Helpers --")) {
            ImGui.textColored(0.8f, 0.9f, 1f, 1f, "Core Scripting Functions:");

            if (ImGui.treeNode(" Events.on(eventName, callback)")) {
                ImGui.textWrapped("Register a callback for a specific event.");
                ImGui.separator();
                ImGui.textColored(0.7f, 1f, 0.7f, 1f, "Example:");
                ImGui.textWrapped("Events.on(\"blockBreak\", { ctx ->\n    Logger.info(\"Block broken: ${ctx.block}\")\n})");
                ImGui.treePop();
            }

            if (ImGui.treeNode(" register(type, id, object)")) {
                ImGui.textWrapped("Register a block, item, or other game object.");
                ImGui.separator();
                ImGui.textColored(0.7f, 1f, 0.7f, 1f, "Example:");
                ImGui.textWrapped("register(\"item\", \"my_mod:banana\", create(Item.class, new Item.Settings()))");
                ImGui.treePop();
            }

            if (ImGui.treeNode(" create(Class, ...args)")) {
                ImGui.textWrapped("Construct a Java object dynamically.");
                ImGui.separator();
                ImGui.textColored(0.7f, 1f, 0.7f, 1f, "Example:");
                ImGui.textWrapped("create(Block.class, Block.Settings.of(Material.STONE))");
                ImGui.treePop();
            }

            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Available Bindings --")) {
            ImGui.text("These objects are available to all scripts:");
            ImGui.bulletText("- Logger - Log messages to console");
            ImGui.bulletText("- ItemRegistryHelper - Register items");
            ImGui.bulletText("- BlockRegistryHelper - Register blocks");
            ImGui.bulletText("- Item, Block, Identifier - Core Minecraft classes");
            ImGui.bulletText("- BlockSettings, FabricBlockSettings - Builders for blocks");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Event Names --")) {
            ImGui.text("Events you can listen to:");
            ImGui.bulletText("- clientTick - Every client tick");
            ImGui.bulletText("- serverTick - Every server tick");
            ImGui.bulletText("- blockBreak - When a player breaks a block");
            ImGui.bulletText("- blockPlace:<id> - When a specific block is placed");
            ImGui.bulletText("- playerJoin - When a player joins");
            ImGui.bulletText("- playerLeave - When a player leaves");
            ImGui.treePop();
        }

        if (ImGui.treeNode("-- Example Script --")) {
            ImGui.text("This script logs a welcome message when a player joins:");
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
