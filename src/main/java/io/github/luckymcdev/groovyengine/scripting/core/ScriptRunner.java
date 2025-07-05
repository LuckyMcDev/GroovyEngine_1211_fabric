package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRunner {
    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();
    private static final boolean IS_DEV_CHECKING_ENABLED = false;

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

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*.groovy")) {
            List<Path> scripts = new ArrayList<>();
            ds.forEach(scripts::add);
            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            for (Path script : scripts) {
                if (ScriptMetadata.isDisabled(script)) continue;

                GroovyEngine.LOGGER.info("Loading {}", script.getFileName());
                GroovyShell shell = ScriptShellFactory.createShell(script);

                try {
                    String content = Files.readString(script, StandardCharsets.UTF_8);

                    shell.evaluate(content, script.getFileName().toString());

                    GroovyEngine.LOGGER.info("Loaded {}", script.getFileName());
                } catch (Exception ex) {
                    String err = "Error in " + script.getFileName() + " – " + ex.getMessage();
                    GroovyEngine.LOGGER.error(err, ex);
                    scriptLoadErrors.add(err);
                }
            }
        } catch (IOException e) {
            String err = "Failed reading scripts: " + e.getMessage();
            GroovyEngine.LOGGER.error(err, e);
            scriptLoadErrors.add(err);
        }
    }
}
