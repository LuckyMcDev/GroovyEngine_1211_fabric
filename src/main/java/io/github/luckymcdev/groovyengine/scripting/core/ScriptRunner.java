package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptRunner {
    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();

    public static void clearErrors() {
        scriptLoadErrors.clear();
    }

    public static void runScriptsInFolder(Path folder) {
        clearErrors();

        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] folder not found: {}", folder);
            return;
        }

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
                    String remapped = remapImports(raw);
                    shell.evaluate(remapped, script.getFileName().toString());
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

    private static String remapImports(String source) {
        // Regex to find import net.minecraft.... or import com.mojang....
        return source
                .replaceAll("(?m)^\\s*import\\s+net\\.minecraft", "import generated.net.minecraft")
                .replaceAll("(?m)^\\s*import\\s+com\\.mojang", "import generated.com.mojang");
    }
}
