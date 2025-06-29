package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptRunner {

    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();

    public static List<String> getScriptLoadErrors() {
        return new ArrayList<>(scriptLoadErrors);
    }

    public static void clearErrors() {
        scriptLoadErrors.clear();
    }

    public static void runScriptsInFolder(Path folder) {
        clearErrors();

        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] Folder does not exist: " + folder.toAbsolutePath());
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.groovy")) {
            List<Path> scripts = new ArrayList<>();
            stream.forEach(scripts::add);

            if (scripts.isEmpty()) {
                GroovyEngine.LOGGER.info("[GroovyEngine] No .groovy scripts found in {}", folder);
                return;
            }

            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            GroovyEngine.LOGGER.info("[GroovyEngine] Script load order in '{}':", folder.getFileName());
            for (Path path : scripts) {
                int prio = ScriptMetadata.getPriority(path);
                GroovyEngine.LOGGER.info("  - {} (priority={})", path.getFileName(), prio);
            }

            for (Path script : scripts) {
                if (ScriptMetadata.isDisabled(script)) {
                    GroovyEngine.LOGGER.info("[GroovyEngine] Skipping disabled script: {}", script.getFileName());
                    continue;
                }

                GroovyEngine.LOGGER.info("[GroovyEngine] Loading script: {}", script.getFileName());
                GroovyShell shell = ScriptShellFactory.createShell(script);
                try {
                    shell.evaluate(script.toFile());
                    GroovyEngine.LOGGER.info("[GroovyEngine] Loaded script: {}", script.getFileName());
                } catch (Exception e) {
                    String errorMsg = "[GroovyEngine] Error loading script: " + script.getFileName() + " - " + e.getMessage();
                    GroovyEngine.LOGGER.error(errorMsg, e);
                    scriptLoadErrors.add(errorMsg);
                }
            }

        } catch (IOException e) {
            String errorMsg = "[GroovyEngine] Failed to read scripts from " + folder + " - " + e.getMessage();
            GroovyEngine.LOGGER.error(errorMsg, e);
            scriptLoadErrors.add(errorMsg);
        }
    }
}
