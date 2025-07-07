package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroovyScriptManager {
    private static final Path ROOT    = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine");
    private static final Path SCRIPTS = ROOT.resolve("scripts");

    // moved out of factory so we can reassign
    private static GroovyShell shell;

    public static void initialize() {
        createScriptFolders();
        shell = ScriptShellFactory.createSharedShell();  // fresh shell + binding
        loadAllScripts();
    }

    public static void reloadScripts() {
        shell = ScriptShellFactory.createSharedShell();  // rebuild shell & binding
        loadAllScripts();
    }

    private static void createScriptFolders() {
        try {
            Files.createDirectories(SCRIPTS.resolve("common"));
            Files.createDirectories(SCRIPTS.resolve("client"));
            Files.createDirectories(SCRIPTS.resolve("server"));
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create script folders", e);
        }
    }

    private static void loadAllScripts() {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();
        runScriptsIn("common");
        if (env == EnvType.CLIENT) runScriptsIn("client");
        else                       runScriptsIn("server");
    }

    private static void runScriptsIn(String subfolder) {
        Path folder = SCRIPTS.resolve(subfolder);
        if (!Files.exists(folder)) return;

        try (Stream<Path> paths = Files.walk(folder)) {
            // filter groovy files that are NOT disabled
            List<Path> scripts = paths
                    .filter(p -> p.toString().endsWith(".groovy"))
                    .filter(p -> !ScriptMetadata.isDisabled(p))
                    .collect(Collectors.toList());

            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            // evaluate scripts in order
            scripts.forEach(GroovyScriptManager::evaluateScript);
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Error walking scripts folder", e);
        }
    }


    private static void evaluateScript(Path script) {
        GroovyEngine.LOGGER.info("[GroovyEngine] Evaluating " + script.getFileName());
        try {
            Script compiledScript = shell.parse(script.toFile());
            compiledScript.run();
        } catch (Exception ex) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Script error in " + script.getFileName(), ex);
        }
    }
}
