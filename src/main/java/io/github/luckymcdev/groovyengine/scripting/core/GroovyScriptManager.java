package io.github.luckymcdev.groovyengine.scripting.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import groovy.lang.*;
import imgui.ImGui;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.scripting.builders.shaders.ShaderBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.shaders.ShaderRegistry;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.*;
import io.github.luckymcdev.groovyengine.scripting.builders.RecipeBuilder;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyEngineScriptUtils;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyLogger;
import io.github.luckymcdev.groovyengine.scripting.gui.GuiBinding;
import io.github.luckymcdev.groovyengine.scripting.input.KeysBinding;
import io.github.luckymcdev.groovyengine.scripting.builders.BlockBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.ItemBuilder;
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
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class GroovyScriptManager {

    private static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine");
    private static final Path SCRIPTS = ROOT.resolve("scripts");

    public static void initialize() {
        createFoldersIfNeeded();
        createDefaultMainScriptIfMissing();
        loadMainScript();
        loadEnvironmentScripts();
    }


    private static void createFoldersIfNeeded() {
        try {
            Files.createDirectories(ROOT.resolve("data/datapacks"));
            Files.createDirectories(ROOT.resolve("data/resourcepacks"));
            Files.createDirectories(SCRIPTS);
            Files.createDirectories(SCRIPTS.resolve("client"));
            Files.createDirectories(SCRIPTS.resolve("server"));
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create script folders", e);
        }
    }

    private static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(
                "java.lang", "java.util", "net.minecraft", "net.minecraft.util",
                "net.minecraft.item", "net.minecraft.block", "net.minecraft.entity",
                "net.minecraft.text", "com.mojang.brigadier"
        );

        // Custom Imports
        imports.addImports("io.github.luckymcdev.groovyengine.scripting.core.GroovyEngineInitializer");

        config.addCompilationCustomizers(imports);

        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        secure.setMethodDefinitionAllowed(true);
        secure.setDisallowedImports(List.of(
                "java.io.*", "java.nio.*", "java.net.*", "javax.*",
                "sun.*", "com.sun.*", "jdk.*", "org.objectweb.*", "org.spongepowered.*"
        ));
        secure.setDisallowedStarImports(List.of(
                "java.io", "java.net", "javax", "sun", "com.sun", "jdk", "org.objectweb", "org.spongepowered"
        ));
        secure.setDisallowedReceivers(List.of("System", "Runtime", "ProcessBuilder", "Thread", "Class", "ClassLoader"));
        secure.setPackageAllowed(true);
        secure.setIndirectImportCheckEnabled(true);

        config.addCompilationCustomizers(secure);
        return config;
    }

    private static GroovyShell createShell(Path scriptPath) {
        Binding binding = new Binding();
        // -- General Use ---

        binding.setVariable("Logger", new GroovyLogger(scriptPath.getFileName().toString()));
        binding.setVariable("Keys", KeysBinding.class);
        binding.setVariable("Gui", GuiBinding.class);
        binding.setVariable("ImGui", ImGui.class);
        binding.setVariable("GeUtils", GroovyEngineScriptUtils.class);
        binding.setVariable("UUID", UUID.class);
        binding.setVariable("Duration", Duration.class);
        binding.setVariable("Math", Math.class);

        // --- Builders ---

        RegistryHelper<Item> itemHelper = new RegistryHelper<>(Registries.ITEM, GroovyEngine.MODID);
        ItemBuilder.setSharedHelper(itemHelper);
        binding.setVariable("ItemBuilder", ItemBuilder.class);

        RegistryHelper<Block> blockHelper = new RegistryHelper<>(Registries.BLOCK, GroovyEngine.MODID);
        BlockBuilder.setSharedHelper(blockHelper);
        binding.setVariable("BlockBuilder", BlockBuilder.class);

        binding.setVariable("RecipeBuilder", RecipeBuilder.class);

        binding.setVariable("ShaderBuilder", ShaderBuilder.class);
        binding.setVariable("ShaderRegistry", ShaderRegistry.class);

        binding.setVariable("BlockBreakEvents", GroovyBlockBreakEvents.class);
        binding.setVariable("BlockPlaceEvents", GroovyBlockPlaceEvents.class);
        binding.setVariable("ClientTickEvents", GroovyClientTickEvents.class);
        binding.setVariable("EntityDeathEvents", GroovyEntityDeathEvents.class);
        binding.setVariable("PlayerInteractItemEvents", GroovyPlayerInteractItemEvents.class);
        binding.setVariable("PlayerJoinEvents", GroovyPlayerJoinEvents.class);
        binding.setVariable("PlayerLeaveEvents", GroovyPlayerLeaveEvents.class);
        binding.setVariable("RegisterBlockEvents", GroovyRegisterBlockEvents.class);
        binding.setVariable("RegisterItemEvents", GroovyRegisterItemEvents.class);
        binding.setVariable("ServerTickEvents", GroovyServerTickEvents.class);


        return new GroovyShell(binding, createCompilerConfig());
    }

    private static void loadMainScript() {
        Path packageJson = ROOT.resolve("groovyengine.package.json");

        if (!Files.exists(packageJson)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] No groovyengine.package.json found.");
            return;
        }

        try (Reader reader = Files.newBufferedReader(packageJson)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String mainClassName = json.get("main").getAsString();

            Path mainScriptPath = SCRIPTS.resolve(mainClassName + ".groovy");
            if (!Files.exists(mainScriptPath)) {
                GroovyEngine.LOGGER.error("[GroovyEngine] Main script file not found: " + mainScriptPath);
                return;
            }

            GroovyShell shell = createShell(mainScriptPath);

            try {
                Script script = shell.parse(mainScriptPath.toFile());
                Object result = script.run(); // executes top-level code and returns last value

                if (result instanceof GroovyEngineInitializer initializer) {
                    initializer.onInitialize();
                } else {
                    GroovyEngine.LOGGER.warn("[GroovyEngine] Main script does not extend GroovyEngineInitializer.");
                }
            } catch (Exception e) {
                GroovyEngine.LOGGER.error("[GroovyEngine] Failed to evaluate main script: " + mainScriptPath.getFileName(), e);
            }

        } catch (Exception e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to load main script", e);
        }
    }

    private static void createDefaultMainScriptIfMissing() {
        Path packageJson = ROOT.resolve("groovyengine.package.json");
        Path mainScriptPath = SCRIPTS.resolve("Main.groovy");

        try {
            if (!Files.exists(packageJson)) {
                JsonObject json = new JsonObject();
                json.addProperty("main", "Main");
                Files.writeString(packageJson, json.toString(), StandardCharsets.UTF_8);
                GroovyEngine.LOGGER.info("[GroovyEngine] Created default groovyengine.package.json");
            }

            if (!Files.exists(mainScriptPath)) {
                String mainScript = """
                import io.github.luckymcdev.groovyengine.scripting.core.GroovyEngineInitializer

                class Main extends GroovyEngineInitializer {
                    @Override
                    void onInitialize() {
                        Logger.info("Hello from Main.groovy!")
                    }
                }
                """;
                Files.writeString(mainScriptPath, mainScript, StandardCharsets.UTF_8);
                GroovyEngine.LOGGER.info("[GroovyEngine] Created default Main.groovy");
            }
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to create default script files", e);
        }
    }


    private static void loadEnvironmentScripts() {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();
        Path folder = env == EnvType.CLIENT ? SCRIPTS.resolve("client") : SCRIPTS.resolve("server"); // ← Fix path
        runScriptsInFolder(folder);
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
                GroovyEngine.LOGGER.info("[GroovyEngine] Loading script: {}", script.getFileName());
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
