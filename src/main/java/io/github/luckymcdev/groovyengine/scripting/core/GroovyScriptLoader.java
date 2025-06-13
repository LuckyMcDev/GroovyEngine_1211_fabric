package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.*;
import imgui.ImGui;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.event.Events;
import io.github.luckymcdev.groovyengine.scripting.registry.RecipeBuilder;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyEngineScriptUtils;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyLogger;
import io.github.luckymcdev.groovyengine.scripting.gui.GuiBinding;
import io.github.luckymcdev.groovyengine.scripting.input.KeysBinding;
import io.github.luckymcdev.groovyengine.scripting.registry.BlockBuilder;
import io.github.luckymcdev.groovyengine.scripting.registry.ItemBuilder;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class GroovyScriptLoader {

    private static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine");

    public static void initialize() {
        createFoldersIfNeeded();
        loadScripts();

        ScriptWatcher.startWatching(ROOT, () -> {
            GroovyEngine.LOGGER.info("[GroovyEngine] Reloading scripts due to file change...");
            Events.clear();
            loadScripts();
        });
    }

    private static void createFoldersIfNeeded() {
        try {
            Files.createDirectories(ROOT.resolve("client"));
            Files.createDirectories(ROOT.resolve("data"));
            Files.createDirectories(ROOT.resolve("data/datapacks"));
            Files.createDirectories(ROOT.resolve("data/resourcepacks"));
            Files.createDirectories(ROOT.resolve("server"));
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create script folders", e);
        }
    }

    private static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();

        // Basic allowed imports
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(
                "java.lang",
                "java.util",
                "net.minecraft",
                "net.minecraft.util",
                "net.minecraft.item",
                "net.minecraft.block",
                "net.minecraft.entity",
                "net.minecraft.text",
                "com.mojang.brigadier"
        );
        config.addCompilationCustomizers(imports);

        // Security customizer: allow only what we know is safe
        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        secure.setMethodDefinitionAllowed(true);

        secure.setDisallowedImports(List.of(
                "java.io.*",
                "java.nio.*",
                "java.net.*",
                "javax.*",
                "sun.*",
                "com.sun.*",
                "jdk.*",
                "org.objectweb.*",
                "org.spongepowered.*"
        ));

        secure.setDisallowedStaticImports(List.of());
        secure.setDisallowedStarImports(List.of(
                "java.io",
                "java.net",
                "javax",
                "sun",
                "com.sun",
                "jdk",
                "org.objectweb",
                "org.spongepowered"
        ));

        // Optional: block access to dangerous receiver classes
        secure.setDisallowedReceivers(List.of(
                "System", "Runtime", "ProcessBuilder", "Thread", "Class", "ClassLoader"
        ));

        // Disallow AST-level unsafe expressions
        secure.setPackageAllowed(true);
        secure.setIndirectImportCheckEnabled(true);

        config.addCompilationCustomizers(secure);

        return config;
    }

    private static GroovyShell createShell(Path scriptPath) {
        Binding binding = new Binding();

        // Per-script logger
        binding.setVariable("Logger", new GroovyLogger(scriptPath.getFileName().toString()));

        // Register items and blocks
        RegistryHelper<Item> itemHelper = new RegistryHelper<>(Registries.ITEM, GroovyEngine.MODID);
        binding.setVariable("ItemBuilder", new Closure<Object>(null) {
            public Object call(Object... args) {
                if (args.length < 1) {
                    throw new IllegalArgumentException("You must provide the item name.");
                }
                String name = args[0].toString();
                return ItemBuilder.register(itemHelper, name);
            }
        });

        RegistryHelper<Block> blockHelper = new RegistryHelper<>(Registries.BLOCK, GroovyEngine.MODID);
        BlockBuilder.setSharedHelper(blockHelper);
        binding.setVariable("BlockBuilder", BlockBuilder.class);

        // Recipes (add this line)
        binding.setVariable("RecipeBuilder", RecipeBuilder.class); // Bind the class directly

        // Events & UI
        binding.setVariable("Events", Events.class);
        binding.setVariable("Keys", new KeysBinding());
        binding.setVariable("Gui", new GuiBinding());
        binding.setVariable("ImGui", new ImGui());

        // Utilities
        binding.setVariable("GeUtils", new GroovyEngineScriptUtils());

        // Frequently used classes
        binding.setVariable("UUID", UUID.class);
        binding.setVariable("Duration", Duration.class);
        binding.setVariable("Math", Math.class);

        return new GroovyShell(binding, createCompilerConfig());
    }

    public static void loadScripts() {
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
