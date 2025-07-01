package io.github.luckymcdev.groovyengine.scripting.builders;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.packs.datagen.LangGenerator;
import io.github.luckymcdev.groovyengine.packs.datagen.ResourcePackDataGenerator;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.function.Consumer;

/**
 * Builder for creating and registering blocks with consistent settings.
 */
public class BlockBuilder {
    private final RegistryHelper<Block> registry;
    private final String name;
    private AbstractBlock.Settings settings;
    private String displayName;
    private String texturePath;
    private Block block;

    private static RegistryHelper<Block> sharedHelper;

    private BlockBuilder(RegistryHelper<Block> registry, String name) {
        this.registry = registry;
        this.name = name;
        this.settings = AbstractBlock.Settings.copy(Blocks.STONE);
    }

    /**
     * Sets the shared registry helper for all BlockBuilder instances.
     */
    public static void setSharedHelper(RegistryHelper<Block> helper) {
        sharedHelper = helper;
    }

    /**
     * Creates a new BlockBuilder with a specific registry.
     */
    public static BlockBuilder register(RegistryHelper<Block> registry, String name) {
        return new BlockBuilder(registry, name);
    }

    /**
     * Creates a new BlockBuilder using the shared registry helper.
     */
    public static BlockBuilder register(String name) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        return new BlockBuilder(sharedHelper, name);
    }

    /**
     * Configures block settings via a consumer.
     */
    public BlockBuilder settings(Consumer<AbstractBlock.Settings> consumer) {
        consumer.accept(this.settings);
        return this;
    }

    /**
     * Sets the display name for the block.
     */
    public BlockBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets a custom texture path for the block.
     */
    public BlockBuilder texture(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    /**
     * Builds and registers the block.
     */
    public Block build() {
        if (block == null) {
            block = new Block(settings);
            registry.register(name, block);

            Item.Settings itemSettings = new Item.Settings();
            BlockItem blockItem = new BlockItem(block, itemSettings);
            new RegistryHelper<>(Registries.ITEM, GroovyEngine.MODID).register(name, blockItem);

            if (displayName != null) {
                LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, displayName);
            } else {
                LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, LangGenerator.toDisplayName(name));
            }

            ResourcePackDataGenerator.generateBlockModel(name, texturePath != null ? texturePath : GroovyEngine.MODID + ":block/" + name);
        }
        return block;
    }
}