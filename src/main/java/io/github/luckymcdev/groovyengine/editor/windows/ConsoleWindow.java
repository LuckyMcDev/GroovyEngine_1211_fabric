package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;
import io.github.luckymcdev.groovyengine.logging.InMemoryLogAppender;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ConsoleWindow {
    public static void render() {
        ImGui.begin("Console");
        for (String line : InMemoryLogAppender.getLogLines()) {
            String[] parts = line.split("\\|", 3);
            if (parts.length >= 3) {
                String level = parts[0].trim();
                String logger = parts[1].trim();
                String message = parts[2].trim();
                String shortLogger = logger.contains(".") ? logger.substring(logger.lastIndexOf('.') + 1) : logger;
                String formatted = String.format("%s | %s | %s", level, shortLogger, message);

                switch (level) {
                    case "ERROR" -> ImGui.textColored(1.0f, 0.25f, 0.25f, 1.0f, "X " + formatted);
                    case "WARN" -> ImGui.textColored(1.0f, 0.75f, 0.2f, 1.0f, "! " + formatted);
                    case "INFO" -> ImGui.textColored(0.6f, 0.8f, 1.0f, 1.0f, "i " + formatted);
                    case "DEBUG" -> ImGui.textColored(0.5f, 1.0f, 0.5f, 1.0f, "D " + formatted);
                    case "TRACE" -> ImGui.textColored(0.7f, 0.7f, 0.7f, 1.0f, "? " + formatted);
                    default -> ImGui.text(line);
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
}
