package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import imgui.ImGui;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.scripting.builders.BlockBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.ItemBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.RecipeBuilder;
import io.github.luckymcdev.groovyengine.scripting.builders.ShaderManager;
import io.github.luckymcdev.groovyengine.scripting.builders.particle.GroovyParticleTypes;
import io.github.luckymcdev.groovyengine.scripting.builders.particle.ParticleBuilder;
import io.github.luckymcdev.groovyengine.scripting.events.*;
import io.github.luckymcdev.groovyengine.scripting.gui.GuiBinding;
import io.github.luckymcdev.groovyengine.scripting.input.KeysBinding;
import io.github.luckymcdev.groovyengine.scripting.utils.Globals;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyEngineScriptUtils;
import io.github.luckymcdev.groovyengine.scripting.utils.GroovyLogger;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;
import io.github.luckymcdev.groovyengine.util.events.ShellBindingEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class ScriptShellFactory {
    /** shared ClassLoader so all scripts see each others' classes */
    public static GroovyClassLoader createClassLoader() {
        return new GroovyClassLoader(ScriptShellFactory.class.getClassLoader());
    }

    public static Binding createBinding() {
        Binding binding = new Binding();
        binding.setVariable("Logger", new GroovyLogger("Global"));// fallback
        binding.setVariable("GeUtils", GroovyEngineScriptUtils.class);
        binding.setVariable("UUID", UUID.class);
        binding.setVariable("Duration", Duration.class);
        binding.setVariable("Math", Math.class);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            binding.setVariable("Gui", GuiBinding.class);
            binding.setVariable("ImGui", ImGui.class);
            binding.setVariable("Keys", KeysBinding.class);
        }

        // Event classes
        binding.setVariable("CommandEvents", CommandEvents.class);
        binding.setVariable("ConnectionEvents", ConnectionEvents.class);
        binding.setVariable("GuiEvents", GuiEvents.class);
        binding.setVariable("PlayerEvents", PlayerEvents.class);
        binding.setVariable("TickEvents", TickEvents.class);
        binding.setVariable("WorldEvents", WorldEvents.class);

        // Builders & registries
        RegistryHelper<Item> itemHelper = new RegistryHelper<>(Registries.ITEM, GroovyEngine.MODID);
        ItemBuilder.setSharedHelper(itemHelper);
        binding.setVariable("ItemBuilder", ItemBuilder.class);

        RegistryHelper<Block> blockHelper = new RegistryHelper<>(Registries.BLOCK, GroovyEngine.MODID);
        BlockBuilder.setSharedHelper(blockHelper);
        binding.setVariable("BlockBuilder", BlockBuilder.class);

        binding.setVariable("RegistryHelper", RegistryHelper.class);

        binding.setVariable("RecipeBuilder", RecipeBuilder.class);
        binding.setVariable("ShaderManager", ShaderManager.class);

        // Vanilla shortcuts
        binding.setVariable("Item", Item.class);
        binding.setVariable("ItemGroups", ItemGroups.class);
        binding.setVariable("ItemSettings", Item.Settings.class);
        binding.setVariable("Block", Block.class);
        binding.setVariable("BlockSettings", net.minecraft.block.AbstractBlock.Settings.class);
        binding.setVariable("Fluid", Fluid.class);

        binding.setVariable("ParticleBuilder", ParticleBuilder.class);
        binding.setVariable("GroovyParticleTypes", GroovyParticleTypes.class);

        binding.setVariable("Globals", Globals.class);

        ShellBindingEvents.BINDING_READY.invoker().onBindingReady(binding);
        return binding;
    }

    public static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(
                "java.lang", "java.util", "net.minecraft", "net.minecraft.util",
                "net.minecraft.item", "net.minecraft.block", "net.minecraft.entity",
                "net.minecraft.text", "com.mojang.brigadier", "net.minecraft.fluid"
        );
        config.addCompilationCustomizers(imports);

        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(true);
        secure.setMethodDefinitionAllowed(true);
        secure.setDisallowedImports(List.of(
                "java.io.*", "java.net.*", "javax.*", "sun.*", "com.sun.*", "jdk.*"
        ));
        secure.setDisallowedReceivers(List.of("System", "Runtime", "Thread", "Class"));
        config.addCompilationCustomizers(secure);

        config.addCompilationCustomizers(new CustomCompilationCustomizer());

        return config;
    }

    /** Build a shared shell for all scripts */
    public static GroovyShell createSharedShell() {
        return new GroovyShell(
                createClassLoader(),
                createBinding(),
                createCompilerConfig()
        );
    }
}