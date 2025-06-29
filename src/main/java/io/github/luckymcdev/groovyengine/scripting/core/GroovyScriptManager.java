package io.github.luckymcdev.groovyengine.scripting.core;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyScriptManager {
    private static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine");
    private static final Path SCRIPTS = ROOT.resolve("scripts");

    public static void initialize() {
        createFoldersIfNeeded();
        loadEnvironmentScripts();
    }

    public static void reloadScripts() {
        loadEnvironmentScripts();
    }

    private static void createFoldersIfNeeded() {
        try {
            Files.createDirectories(ROOT.resolve("data/datapacks"));
            Files.createDirectories(ROOT.resolve("data/resourcepacks"));
            Files.createDirectories(SCRIPTS.resolve("common"));
            Files.createDirectories(SCRIPTS.resolve("client"));
            Files.createDirectories(SCRIPTS.resolve("server"));
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create script folders", e);
        }
    }

    private static void loadEnvironmentScripts() {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();

        ScriptRunner.runScriptsInFolder(SCRIPTS.resolve("common"));

        if (env == EnvType.CLIENT) {
            ScriptRunner.runScriptsInFolder(SCRIPTS.resolve("client"));
        } else {
            ScriptRunner.runScriptsInFolder(SCRIPTS.resolve("server"));
        }
    }
}
