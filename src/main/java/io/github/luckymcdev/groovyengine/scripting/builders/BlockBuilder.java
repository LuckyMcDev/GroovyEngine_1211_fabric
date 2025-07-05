package io.github.luckymcdev.groovyengine.scripting.builders;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.packs.datagen.LangGenerator;
import io.github.luckymcdev.groovyengine.packs.datagen.ResourcePackDataGenerator;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;
import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockBuilder {
    private final RegistryHelper<Block> registry;
    private final String name;
    private Block block;
    private BlockBehaviour.Properties settings;

    private Class<? extends Block> blockClass = Block.class; // default vanilla Block class

    private static RegistryHelper<Block> sharedHelper;

    private BlockBuilder(RegistryHelper<Block> registry, String name) {
        this.registry = registry;
        this.name = name;
        this.settings = BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE);
    }

    public static void setSharedHelper(RegistryHelper<Block> helper) {
        sharedHelper = helper;
    }

    public static BlockBuilder register(RegistryHelper<Block> registry, String name) {
        return new BlockBuilder(registry, name);
    }

    public static BlockBuilder register(String name) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        return new BlockBuilder(sharedHelper, name);
    }

    /**
     * Register a fully constructed custom Block instance directly.
     */
    public static BlockBuilder registerCustom(String name, Block customBlock) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        BlockBuilder builder = new BlockBuilder(sharedHelper, name);
        builder.block = customBlock;
        return builder;
    }

    public BlockBuilder settings(Consumer<BlockBehaviour.Properties> consumer) {
        consumer.accept(this.settings);
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

    public BlockBuilder blockClass(Class<? extends Block> clazz) {
        this.blockClass = clazz;
        return this;
    }

    private String displayName;
    private String texturePath;

    public Block build() {
        if (block == null) {
            try {
                Constructor<? extends Block> ctor = blockClass.getConstructor(BlockBehaviour.Properties.class);
                block = ctor.newInstance(settings);
            } catch (NoSuchMethodException e) {
                try {
                    block = blockClass.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate block class: " + blockClass, ex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate block class: " + blockClass, e);
            }
        }

        registry.register(name, block);

        Item.Properties itemSettings = new Item.Properties();
        BlockItem blockItem = new BlockItem(block, itemSettings);
        new RegistryHelper<>(BuiltInRegistries.ITEM, GroovyEngine.MODID).register(name, blockItem);

        if (displayName != null) {
            LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, displayName);
        } else {
            LangGenerator.addLangEntry("block." + GroovyEngine.MODID + "." + name, LangGenerator.toDisplayName(name));
        }

        ResourcePackDataGenerator.generateBlockModel(name, texturePath != null ? texturePath : GroovyEngine.MODID + ":block/" + name);

        return block;
    }
}
