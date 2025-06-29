package io.github.luckymcdev.groovyengine.scripting.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScriptMetadata {
    public static int getPriority(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("//priority=")) {
                    return Integer.parseInt(line.substring("//priority=".length()).trim());
                }
                if (!line.isEmpty()) break; // Stop after first non-empty non-metadata line
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static boolean isDisabled(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("//disabled")) return true;
                break;
            }
        } catch (IOException ignored) {}
        return false;
    }
}

