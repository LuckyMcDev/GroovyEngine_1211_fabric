package io.github.luckymcdev.api.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.luckymcdev.GroovyEngine;
import io.github.luckymcdev.api.RegistryHelper;
import io.github.luckymcdev.api.scripting.exposed.GroovyEngineContext;
import io.github.luckymcdev.api.scripting.exposed.GroovyLogger;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.api.EnvType;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyScriptLoader {

    private static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine");

    public static void initialize() {
        createFoldersIfNeeded();
        loadScripts();
    }

    private static void createFoldersIfNeeded() {
        try {
            Files.createDirectories(ROOT.resolve("libs"));
            Files.createDirectories(ROOT.resolve("client"));
            Files.createDirectories(ROOT.resolve("server"));
            Files.createDirectories(ROOT.resolve("shared"));
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create script folders", e);
        }
    }

    private static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();

        config.addCompilationCustomizers(imports);
        return config;
    }

    private static GroovyShell createShell(Path scriptPath) {
        Binding binding = new Binding();

        // Per-script logger
        binding.setVariable("Logger", new GroovyLogger(scriptPath.getFileName().toString()));

        // Item registering
        binding.setVariable("ItemRegistryHelper", new RegistryHelper<Item>(Registries.ITEM, "groovyengine"));
        binding.setVariable("Item", Item.class);
        binding.setVariable("ItemSettings", Item.Settings.class);




        // Shared API context
        binding.setVariable("ctx", new GroovyEngineContext());

        return new GroovyShell(binding, createCompilerConfig());
    }

    public static void loadScripts() {
        // Load shared libs first
        runScriptsInFolder(ROOT.resolve("libs"));
        runScriptsInFolder(ROOT.resolve("shared"));

        // Then load based on environment (client/server)
        EnvType env = FabricLoader.getInstance().getEnvironmentType();

        if (env == EnvType.CLIENT) {
            runScriptsInFolder(ROOT.resolve("client"));
        } else {
            runScriptsInFolder(ROOT.resolve("server"));
        }
    }

    private static void runScriptsInFolder(Path folder) {
        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] Folder does not exist: " + folder.toAbsolutePath());
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.groovy")) {
            boolean foundScripts = false;
            for (Path script : stream) {
                foundScripts = true;
                GroovyEngine.LOGGER.info("[GroovyEngine] Attempting to load script: {}", script.getFileName());
                GroovyShell shell = createShell(script);
                try {
                    shell.evaluate(script.toFile());
                    GroovyEngine.LOGGER.info("[GroovyEngine] Loaded script: {}", script.getFileName());
                } catch (Exception e) {
                    GroovyEngine.LOGGER.error("[GroovyEngine] Error loading script: " + script.getFileName(), e);
                }
            }
            if (!foundScripts) {
                GroovyEngine.LOGGER.info("[GroovyEngine] No .groovy scripts found in folder: " + folder.toAbsolutePath());
            }
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to read scripts from folder: " + folder.toAbsolutePath(), e);
        }
    }

}
