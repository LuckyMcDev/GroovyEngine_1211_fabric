package io.github.luckymcdev.api.scripting.registry;

import io.github.luckymcdev.GroovyEngine;
import io.github.luckymcdev.generators.LangGenerator;
import io.github.luckymcdev.generators.ResourcePackDataGenerator;
import io.github.luckymcdev.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;

public class BlockBuilder {
    private final RegistryHelper<Block> registry;
    private final String name;
    private AbstractBlock.Settings settings;
    private String displayName;
    private String texturePath;  // e.g. "groovyengine:block/banana_block"

    private Block block;

    private static RegistryHelper<Block> sharedHelper;

    public BlockBuilder(RegistryHelper<Block> registry, String name) {
        this.registry = registry;
        this.name = name;
        this.settings = AbstractBlock.Settings.copy(Blocks.STONE);
    }

    public static BlockBuilder register(RegistryHelper<Block> registry, String name) {
        return new BlockBuilder(registry, name);
    }

    public static void setSharedHelper(RegistryHelper<Block> helper) {
        sharedHelper = helper;
    }

    public static BlockBuilder register(String name) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        return new BlockBuilder(sharedHelper, name);
    }

    public BlockBuilder settings(AbstractBlock.Settings settings) {
        // Optional to allow custom settings
        // (e.g. material, hardness, luminance, etc)
        this.settings = settings;
        return this;
    }

    public BlockBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public BlockBuilder texture(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public Block build() {
        if (block == null) {
            block = new Block(settings);
            registry.register(name, block);

            // Lang entry
            if (displayName != null) {
                LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, displayName);
            } else {
                LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, LangGenerator.toDisplayName(name));
            }

            // Generate block model JSON for blockstate and model
            ResourcePackDataGenerator.generateBlockModel(name, texturePath != null ? texturePath : GroovyEngine.MODID + ":block/" + name);
        }
        return block;
    }
}
