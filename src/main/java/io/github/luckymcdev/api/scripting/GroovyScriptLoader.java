package io.github.luckymcdev.api.scripting;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import io.github.luckymcdev.GroovyEngine;
import io.github.luckymcdev.api.scripting.event.Events;
import io.github.luckymcdev.util.RegistryHelper;
import net.minecraft.block.Block;
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

        ScriptWatcher.startWatching(ROOT, () -> {
            GroovyEngine.LOGGER.info("[GroovyEngine] Reloading scripts due to file change...");
            Events.clear(); // Clear old listeners before reloading
            loadScripts();
        });
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

        // Registry helpers for Items and Blocks
        binding.setVariable("ItemRegistryHelper", new RegistryHelper<Item>(Registries.ITEM, "groovyengine"));
        binding.setVariable("BlockRegistryHelper", new RegistryHelper<Block>(Registries.BLOCK, "groovyengine"));

        // Common Minecraft classes for scripting convenience
        binding.setVariable("Item", Item.class);
        binding.setVariable("Block", Block.class);
        binding.setVariable("BlockSettings", Block.Settings.class);
        binding.setVariable("FabricBlockSettings", net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings.class);
        binding.setVariable("Identifier", net.minecraft.util.Identifier.class);
        binding.setVariable("Blocks", net.minecraft.block.Blocks.class);
        binding.setVariable("Identifier", net.minecraft.util.Identifier.class);

        binding.setVariable("Events", Events.class);



        // Shared API context and utilities
        binding.setVariable("ctx", new GroovyEngineContext());

        // Register function for flexible registrations
        binding.setVariable("register", new Closure<Object>(null) {
            public Object call(String type, String id, Object obj) {
                switch(type.toLowerCase()) {
                    case "item":
                        ((RegistryHelper<Item>) binding.getVariable("ItemRegistryHelper")).register(id, (Item)obj);
                        break;
                    case "block":
                        ((RegistryHelper<Block>) binding.getVariable("BlockRegistryHelper")).register(id, (Block)obj);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown registration type: " + type);
                }
                GroovyEngine.LOGGER.info("[GroovyEngine] Registered " + type + ": " + id);
                return obj;
            }
        });

        binding.setVariable("create", new Closure<Object>(null) {
            public Object call(Object clazzObj, Object... args) {
                if (!(clazzObj instanceof Class<?> clazz)) {
                    throw new IllegalArgumentException("First argument must be a Class");
                }
                try {
                    return org.codehaus.groovy.runtime.InvokerHelper.invokeConstructorOf(clazz, args);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
                }
            }
        });



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
