package io.github.luckymcdev.groovyengine.scripting.builders;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.packs.datagen.LangGenerator;
import io.github.luckymcdev.groovyengine.packs.datagen.ResourcePackDataGenerator;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;
import net.minecraft.item.Item;

import java.util.function.Consumer;

public class ItemBuilder {
    private final RegistryHelper<Item> registry;
    private final String name;
    private Item.Settings settings;
    private String displayName;
    private String texturePath;

    private Item item;

    private static RegistryHelper<Item> sharedHelper;

    private ItemBuilder(RegistryHelper<Item> registry, String name) {
        this.registry = registry;
        this.name = name;
        this.settings = new Item.Settings();
    }

    public static void setSharedHelper(RegistryHelper<Item> helper) {
        sharedHelper = helper;
    }

    public static ItemBuilder register(RegistryHelper<Item> registry, String name) {
        return new ItemBuilder(registry, name);
    }

    public static ItemBuilder register(String name) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        return new ItemBuilder(sharedHelper, name);
    }

    public static ItemBuilder registerCustom(String name, Item customItem) {
        if (sharedHelper == null) throw new IllegalStateException("Shared helper not set!");
        ItemBuilder builder = new ItemBuilder(sharedHelper, name);
        builder.item = customItem; // directly assign the custom item
        return builder;
    }

    public ItemBuilder settings(Consumer<Item.Settings> consumer) {
        consumer.accept(this.settings);
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder texture(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public Item build() {
        if (item == null) {
            item = new Item(settings);
        }

        registry.register(name, item);
        ResourcePackDataGenerator.generateItemModel(name, GroovyEngine.MODID + ":item/" + name);

        if (displayName != null) {
            LangGenerator.addLangEntry("item." + GroovyEngine.MODID + "." + name, displayName);
        } else {
            LangGenerator.addLangEntry("item." + GroovyEngine.MODID + "." + name, LangGenerator.toDisplayName(name));
        }

        return item;
    }
}
