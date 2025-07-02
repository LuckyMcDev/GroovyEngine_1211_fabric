package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.util.TinyRemapper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptRunner {

    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();
    private static TinyRemapper tinyRemapper;

    // Add a method to set the TinyRemapper instance
    public static void setTinyRemapper(TinyRemapper remapper) {
        tinyRemapper = remapper;
    }

    public static List<String> getScriptLoadErrors() {
        return new ArrayList<>(scriptLoadErrors);
    }

    public static void clearErrors() {
        scriptLoadErrors.clear();
    }

    private static String remapImports(Path script) throws IOException {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return Files.readString(script, StandardCharsets.UTF_8);
        }

        if (tinyRemapper == null) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] TinyRemapper not initialized, skipping remapping");
            return Files.readString(script, StandardCharsets.UTF_8);
        }

        List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
        List<String> out = new ArrayList<>(lines.size());

        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("import net.minecraft.")) {
                out.add(line);
                continue;
            }

            String decl = trimmed.substring(7).replaceFirst(";$", "");
            String className;
            String alias;

            if (decl.contains(" as ")) {
                String[] parts = decl.split("\\s+as\\s+");
                className = parts[0];
                alias = parts[1];
            } else {
                className = decl;
                alias = className.substring(className.lastIndexOf('.') + 1);
            }

            try {
                String classPath = className.replace('.', '/');
                String remapped = tinyRemapper.mapToObfuscated(classPath);
                String remappedClassName = remapped.replace('/', '.');

                out.add("import " + remappedClassName + " as " + alias);
                GroovyEngine.LOGGER.debug("[GroovyEngine] Remapped {} -> {}", className, remappedClassName);
            } catch (Throwable t) {
                GroovyEngine.LOGGER.warn("[GroovyEngine] could not map {}: {}", className, t.getMessage());
                out.add(line);
            }
        }

        return String.join("\n", out);
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
                    String processedSource = remapImports(script);
                    shell.evaluate(processedSource, script.getFileName().toString());
                    GroovyEngine.LOGGER.debug("Final remapped source:\n{}", processedSource);
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