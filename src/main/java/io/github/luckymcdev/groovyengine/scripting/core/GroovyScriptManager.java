package io.github.luckymcdev.groovyengine.scripting.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import groovy.lang.*;
import imgui.ImGui;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.scripting.builders.shaders.ShaderBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.shaders.ShaderRegistry;
import io.github.luckymcdev.groovyengine.scripting.events.*;
import io.github.luckymcdev.groovyengine.scripting.builders.RecipeBuilder;
import io.github.luckymcdev.groovyengine.scripting.security.SandboxClassLoader;
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
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;

import net.minecraft.util.ActionResult;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

    private static SandboxClassLoader createSandboxLoader(Path scriptBaseDir) {
        URL[] scriptUrls = new URL[]{};
        try {

            scriptUrls = new URL[]{scriptBaseDir.toUri().toURL()};
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to convert script base directory to URL: {}", scriptBaseDir, e);
        }

        ClassLoader parentClassLoader = GroovyScriptManager.class.getClassLoader();

        return new SandboxClassLoader(scriptUrls, parentClassLoader);
    }

    private static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();

        imports.addStarImports(
                "java.lang", "java.util", "net.minecraft", "net.minecraft.util",
                "net.minecraft.item", "net.minecraft.block", "net.minecraft.entity",
                "net.minecraft.text", "com.mojang.brigadier"
        );

// Add these explicit imports:
        imports.addImports(
                "net.minecraft.block.Blocks",
                "net.minecraft.item.ItemStack",
                "net.minecraft.util.ActionResult",
                "net.minecraft.util.TypedActionResult"
        );

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

        // --- General Use ---
        binding.setVariable("Logger", new GroovyLogger(scriptPath.getFileName().toString()));
        binding.setVariable("GeUtils", GroovyEngineScriptUtils.class);
        binding.setVariable("UUID", UUID.class);
        binding.setVariable("Duration", Duration.class);
        binding.setVariable("Math", Math.class);

        // --- Only bind client classes on CLIENT ---
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            binding.setVariable("Gui", GuiBinding.class);
            binding.setVariable("ImGui", ImGui.class);
            binding.setVariable("Keys", KeysBinding.class);
        }

        // --- Events ---
        binding.setVariable("CommandEvents", CommandEvents.class);
        binding.setVariable("ConnectionEvents", ConnectionEvents.class);
        binding.setVariable("GuiEvents", GuiEvents.class);
        binding.setVariable("PlayerEvents", PlayerEvents.class);
        binding.setVariable("TickEvents", TickEvents.class);
        binding.setVariable("WorldEvents", WorldEvents.class);

        binding.setVariable("Blocks", net.minecraft.block.Blocks.class);
        binding.setVariable("ItemStack", net.minecraft.item.ItemStack.class);
        binding.setVariable("ActionResult", net.minecraft.util.ActionResult.class);
        binding.setVariable("TypedActionResult", net.minecraft.util.TypedActionResult.class);

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

        // --- Minecraft types ---
        binding.setVariable("Item", Item.class);
        binding.setVariable("ItemGroups", ItemGroups.class);
        binding.setVariable("ItemSettings", Item.Settings.class);

        binding.setVariable("Block", Block.class);
        binding.setVariable("AbstractBlock", net.minecraft.block.AbstractBlock.class);
        binding.setVariable("BlockSettings", net.minecraft.block.AbstractBlock.Settings.class);

        binding.setVariable("StatusEffect", net.minecraft.entity.effect.StatusEffect.class);
        binding.setVariable("StatusEffects", net.minecraft.entity.effect.StatusEffects.class);

        binding.setVariable("SoundEvents", net.minecraft.sound.SoundEvents.class);
        binding.setVariable("ParticleTypes", net.minecraft.particle.ParticleTypes.class);

        binding.setVariable("Text", net.minecraft.text.Text.class);
        binding.setVariable("Identifier", net.minecraft.util.Identifier.class);
        binding.setVariable("DyeColor", net.minecraft.util.DyeColor.class);
        binding.setVariable("Hand", net.minecraft.util.Hand.class);
        binding.setVariable("ActionResult", ActionResult.class);
        binding.setVariable("BlockPos", net.minecraft.util.math.BlockPos.class);
        binding.setVariable("Direction", net.minecraft.util.math.Direction.class);

        return new GroovyShell(binding, createCompilerConfig());
    }



    private static void loadEnvironmentScripts() {
        EnvType env = FabricLoader.getInstance().getEnvironmentType();

        // Always load common scripts first
        runScriptsInFolder(SCRIPTS.resolve("common"));

        // Then load environment-specific ones
        if (env == EnvType.CLIENT) {
            runScriptsInFolder(SCRIPTS.resolve("client"));
        } else {
            runScriptsInFolder(SCRIPTS.resolve("server"));
        }
    }

    private static void runScriptsInFolder(Path folder) {
        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] Folder does not exist: " + folder.toAbsolutePath());
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.groovy")) {
            List<Path> scripts = new ArrayList<>();
            for (Path path : stream) {
                scripts.add(path);
            }

            if (scripts.isEmpty()) {
                GroovyEngine.LOGGER.info("[GroovyEngine] No .groovy scripts found in folder: " + folder.toAbsolutePath());
                return;
            }

            // Sort by priority from first-line comment
            scripts.sort(Comparator.comparingInt(GroovyScriptManager::getPriority));

            // Log order
            GroovyEngine.LOGGER.info("[GroovyEngine] Script load order in folder '{}':", folder.getFileName());
            for (Path path : scripts) {
                int prio = getPriority(path);
                GroovyEngine.LOGGER.info("  - {} (priority={})", path.getFileName(), prio);
            }

            for (Path script : scripts) {
                if (isDisabledScript(script)) {
                    GroovyEngine.LOGGER.info("[GroovyEngine] Skipping disabled script: {}", script.getFileName());
                    continue;
                }

                GroovyEngine.LOGGER.info("[GroovyEngine] Loading script: {}", script.getFileName());
                GroovyShell shell = createShell(script);
                try {
                    shell.evaluate(script.toFile());
                    GroovyEngine.LOGGER.info("[GroovyEngine] Loaded script: {}", script.getFileName());
                } catch (Exception e) {
                    GroovyEngine.LOGGER.error("[GroovyEngine] Error loading script: " + script.getFileName(), e);
                }
            }

        } catch (IOException e) {
            GroovyEngine.LOGGER.error("[GroovyEngine] Failed to read scripts from folder: " + folder.toAbsolutePath(), e);
        }
    }


    private static int getPriority(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            if (!lines.isEmpty()) {
                String first = lines.get(0).trim();
                if (first.startsWith("//priority=")) {
                    return Integer.parseInt(first.substring("//priority=".length()).trim());
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private static boolean isDisabledScript(Path script) {
        try {
            List<String> lines = Files.readAllLines(script, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("//disabled")) return true;
                break; // Stop after first non-empty line
            }
        } catch (IOException ignored) {}
        return false;
    }
}
