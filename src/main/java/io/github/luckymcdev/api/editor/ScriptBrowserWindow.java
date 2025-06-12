package io.github.luckymcdev.api.editor;

import imgui.ImGui;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScriptBrowserWindow {
    public static void render() {
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
                        if (ImGui.selectable(relativePath)) {
                            ScriptEditor.openScript(path);
                        }
                    });
        } catch (IOException e) {
            ImGui.textColored(1.0f, 0.2f, 0.2f, 1.0f, "Failed to read GroovyEngine scripts");
            ImGui.textWrapped(e.getMessage());
        }

        ImGui.end();
    }
}
