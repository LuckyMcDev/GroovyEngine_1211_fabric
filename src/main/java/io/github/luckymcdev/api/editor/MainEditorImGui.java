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

    public static void render() {

        if (EditorState.open) {
            MinecraftClient.getInstance().mouse.unlockCursor();

        } else {
            MinecraftClient.getInstance().mouse.lockCursor();
        }

        ImGuiImpl.draw(io -> {

            ImGui.begin("Console");

            for (String line : InMemoryLogAppender.getLogLines()) {
                if (line.contains("ERROR")) {
                    ImGui.textColored(1.0f, 0.25f, 0.25f, 1.0f, "X" + line);
                } else if (line.contains("WARN")) {
                    ImGui.textColored(1.0f, 0.75f, 0.2f, 1.0f, "! " + line);
                } else if (line.contains("INFO")) {
                    ImGui.textColored(0.6f, 0.8f, 1.0f, 1.0f, "i " + line);
                } else if (line.contains("DEBUG")) {
                    ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, "D " + line);
                } else if (line.contains("TRACE")) {
                    ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "? " + line);
                } else {
                    ImGui.text(line); // fallback
                }
            }

            if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
                ImGui.setScrollHereY(1.0f);
            }

            ImGui.end();


            ImGui.begin("Script Browser");

            Path scriptsRoot = MinecraftClient.getInstance()
                    .runDirectory
                    .toPath()
                    .resolve("GroovyEngine");

            try (Stream<Path> files = Files.walk(scriptsRoot)) {
                files.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".groovy")) // optional filter
                        .forEach(path -> {
                            String relativePath = scriptsRoot.relativize(path).toString();
                            ImGui.bulletText(relativePath);
                        });
            } catch (IOException e) {
                ImGui.textColored(1.0f, 0.2f, 0.2f, 1.0f, "Failed to read GroovyEngine scripts");
                ImGui.textWrapped(e.getMessage());
            }

            ImGui.end();


            ImGui.begin("Blockdisplay Browser");
            ImGui.end();
        });
    }
}
