package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.Binding;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class  ScriptShellFactory {
    public static GroovyShell createShell(Path scriptPath) {
        Binding binding = new Binding();

        binding.setVariable("Logger", new GroovyLogger(scriptPath.getFileName().toString()));
        binding.setVariable("GeUtils", GroovyEngineScriptUtils.class);
        binding.setVariable("UUID", UUID.class);
        binding.setVariable("Duration", Duration.class);
        binding.setVariable("Math", Math.class);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            binding.setVariable("Gui", GuiBinding.class);
            binding.setVariable("ImGui", ImGui.class);
            binding.setVariable("Keys", KeysBinding.class);
        }

        binding.setVariable("CommandEvents", CommandEvents.class);
        binding.setVariable("ConnectionEvents", ConnectionEvents.class);
        binding.setVariable("GuiEvents", GuiEvents.class);
        binding.setVariable("PlayerEvents", PlayerEvents.class);
        binding.setVariable("TickEvents", TickEvents.class);
        binding.setVariable("WorldEvents", WorldEvents.class);

        binding.setVariable("Blocks", net.minecraft.world.level.block.Blocks.class);
        binding.setVariable("ItemStack", net.minecraft.world.item.ItemStack.class);
        binding.setVariable("ActionResult", net.minecraft.world.InteractionResult.class);
        binding.setVariable("TypedActionResult", net.minecraft.world.InteractionResultHolder.class);

        RegistryHelper<Item> itemHelper = new RegistryHelper<>(BuiltInRegistries.ITEM, GroovyEngine.MODID);
        ItemBuilder.setSharedHelper(itemHelper);
        binding.setVariable("ItemBuilder", ItemBuilder.class);

        RegistryHelper<Block> blockHelper = new RegistryHelper<>(BuiltInRegistries.BLOCK, GroovyEngine.MODID);
        BlockBuilder.setSharedHelper(blockHelper);
        binding.setVariable("BlockBuilder", BlockBuilder.class);

        binding.setVariable("RecipeBuilder", RecipeBuilder.class);

        binding.setVariable("ShaderManager", ShaderManager.class);

        binding.setVariable("Item", Item.class);
        binding.setVariable("ItemGroups", CreativeModeTabs.class);
        binding.setVariable("ItemSettings", Item.Properties.class);
        binding.setVariable("Block", Block.class);
        binding.setVariable("BlockSettings", net.minecraft.world.level.block.state.BlockBehaviour.Properties.class);

        binding.setVariable("ParticleBuilder", ParticleBuilder.class);
        binding.setVariable("GroovyParticleTypes", GroovyParticleTypes.class);

        binding.setVariable("Globals", Globals.class);

        ShellBindingEvents.BINDING_READY.invoker().onBindingReady(binding);

        return new GroovyShell(binding, createCompilerConfig());
    }

    private static CompilerConfiguration createCompilerConfig() {
        CompilerConfiguration config = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();

        imports.addStarImports(
                "java.lang", "java.util", "net.minecraft", "net.minecraft.util",
                "net.minecraft.item", "net.minecraft.block", "net.minecraft.entity",
                "net.minecraft.text", "com.mojang.brigadier"
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

        return config;
    }
}

