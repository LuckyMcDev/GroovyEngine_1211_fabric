package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.util.mappings.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptRunner {
    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();

    // Shared parser for production remapping
    private static final ScriptParser SCRIPT_PARSER;

    static {
        try {
            MappingsParser mappings = new MappingsParser("assets/groovyengine/tiny/mappings.json");
            SCRIPT_PARSER = new ScriptParser(mappings);
            GroovyEngine.LOGGER.info("[GroovyEngine] Mappings loaded for production.");
        } catch (Exception e) {
            throw new RuntimeException("[GroovyEngine] Failed to load mappings.json", e);
        }
    }

    public static List<String> getScriptLoadErrors() {
        return new ArrayList<>(scriptLoadErrors);
    }

    public static void clearErrors() {
        scriptLoadErrors.clear();
    }

    public static void runScriptsInFolder(Path folder) {
        clearErrors();

        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] folder not found: {}", folder);
            return;
        }

        boolean isDev = FabricLoader.getInstance().isDevelopmentEnvironment();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*.groovy")) {
            List<Path> scripts = new ArrayList<>();
            ds.forEach(scripts::add);
            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            for (Path script : scripts) {
                if (ScriptMetadata.isDisabled(script)) continue;

                GroovyEngine.LOGGER.info("[GroovyEngine] Loading {}", script.getFileName());
                GroovyShell shell = ScriptShellFactory.createShell(script);

                try {
                    String raw = Files.readString(script, StandardCharsets.UTF_8);
                    String toEvaluate = raw;

                    if (!isDev) {
                        // **only** remap in production
                        toEvaluate = SCRIPT_PARSER.remapScript(raw);

                        GroovyEngine.LOGGER.warn("=== Remapped Script ===\n{}", toEvaluate);
                    }

                    shell.evaluate(toEvaluate, script.getFileName().toString());
                    GroovyEngine.LOGGER.info("[GroovyEngine] Loaded {}", script.getFileName());

                } catch (Exception ex) {
                    String err = "[GroovyEngine] Error in " + script.getFileName() + " – " + ex.getMessage();
                    GroovyEngine.LOGGER.error(err, ex);
                    scriptLoadErrors.add(err);
                }
            }
        } catch (IOException e) {
            String err = "[GroovyEngine] Failed reading scripts: " + e.getMessage();
            GroovyEngine.LOGGER.error(err, e);
            scriptLoadErrors.add(err);
        }
    }
}
