package io.github.luckymcdev.api.editor;

import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import imgui.ImGui;
import io.github.luckymcdev.api.logging.InMemoryLogAppender;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MainEditorImGui {

    private static boolean showConsole = true;
    private static boolean showScripts = true;
    private static boolean showDocs = true;
    private static boolean showBlockBrowser = true;

    public static void render() {
        if (EditorState.open) {
            MinecraftClient.getInstance().mouse.unlockCursor();
        } else {
            MinecraftClient.getInstance().mouse.lockCursor();
        }

        ImGuiImpl.draw(io -> {
            renderMenuBar();

            if (showConsole) renderConsole();
            if (showScripts) renderScriptBrowser();
            if (showDocs) renderDocs();
            if (showBlockBrowser) renderBlockBrowser();
        });
    }

    private static void renderMenuBar() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("GroovyEngine")) {
                ImGui.menuItem("Console", "", showConsole, true);
                ImGui.menuItem("Script Browser", "", showScripts, true);
                ImGui.menuItem("Docs", "", showDocs, true);
                ImGui.menuItem("Block Browser", "", showBlockBrowser, true);
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Theme")) {
                // TODO: Add themes or styling later
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }

        // Toggle handling
        showConsole = ImGui.menuItem("Console", null, showConsole);
        showScripts = ImGui.menuItem("Script Browser", null, showScripts);
        showDocs = ImGui.menuItem("Docs", null, showDocs);
        showBlockBrowser = ImGui.menuItem("Block Browser", null, showBlockBrowser);
    }

    private static void renderConsole() {
        ImGui.begin("Console");

        for (String line : InMemoryLogAppender.getLogLines()) {
            String[] parts = line.split("\\|", 3);
            if (parts.length >= 3) {
                String level = parts[0].trim();
                String logger = parts[1].trim();
                String message = parts[2].trim();
                String shortLogger = logger.contains(".") ? logger.substring(logger.lastIndexOf('.') + 1) : logger;
                String formatted = String.format("%s | %s | %s", level, shortLogger, message);

                if (level.contains("ERROR")) {
                    ImGui.textColored(1.0f, 0.25f, 0.25f, 1.0f, "X " + formatted);
                } else if (level.contains("WARN")) {
                    ImGui.textColored(1.0f, 0.75f, 0.2f, 1.0f, "! " + formatted);
                } else if (level.contains("INFO")) {
                    ImGui.textColored(0.6f, 0.8f, 1.0f, 1.0f, "i " + formatted);
                } else if (level.contains("DEBUG")) {
                    ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, "D " + formatted);
                } else if (level.contains("TRACE")) {
                    ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "? " + formatted);
                } else {
                    ImGui.text(line);
                }
            } else {
                ImGui.text(line);
            }
        }

        if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY(1.0f);
        }

        ImGui.end();
    }

    private static void renderScriptBrowser() {
        ImGui.begin("Script Browser");

        Path scriptsRoot = MinecraftClient.getInstance()
                .runDirectory
                .toPath()
                .resolve("GroovyEngine");

        try (Stream<Path> files = Files.walk(scriptsRoot)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".groovy"))
                    .forEach(path -> {
                        String relativePath = scriptsRoot.relativize(path).toString();
                        ImGui.bulletText(relativePath);
                    });
        } catch (IOException e) {
            ImGui.textColored(1.0f, 0.2f, 0.2f, 1.0f, "Failed to read GroovyEngine scripts");
            ImGui.textWrapped(e.getMessage());
        }

        ImGui.end();
    }

    private static void renderBlockBrowser() {
        ImGui.begin("Block Browser");
        ImGui.text("TODO: Implement block display browser...");
        ImGui.end();
    }

    private static void renderDocs() {
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
