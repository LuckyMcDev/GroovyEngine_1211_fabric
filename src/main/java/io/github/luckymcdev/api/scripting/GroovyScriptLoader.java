package io.github.luckymcdev.api.scripting;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import imgui.ImGui;
import io.github.luckymcdev.GroovyEngine;
import io.github.luckymcdev.api.scripting.event.Events;
import io.github.luckymcdev.api.scripting.gui.GuiBinding;
import io.github.luckymcdev.api.scripting.input.KeysBinding;
import io.github.luckymcdev.api.scripting.registry.ItemRegistrar;
import io.github.luckymcdev.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
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

        RegistryHelper<Item> itemHelper = new RegistryHelper<>(Registries.ITEM, "groovyengine");
        ItemRegistrar registrar = new ItemRegistrar(itemHelper);
        binding.setVariable("ItemRegistrar", registrar);

        binding.setVariable("Events", Events.class);

        binding.setVariable("Keys", new KeysBinding());
        binding.setVariable("Gui", new GuiBinding());
        binding.setVariable("ImGui", new ImGui());


        // Shared API context and utilities
        binding.setVariable("GEctx", new GroovyEngineContext());

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
