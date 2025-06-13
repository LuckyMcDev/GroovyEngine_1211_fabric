package io.github.luckymcdev.groovyengine.scripting.registry;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.generators.LangGenerator;
import io.github.luckymcdev.groovyengine.generators.ResourcePackDataGenerator;
import io.github.luckymcdev.groovyengine.util.RegistryHelper;
import net.minecraft.item.Item;

public class ItemBuilder {
    private final RegistryHelper<Item> registry;
    private final String name;
    private Item.Settings settings;
    private String displayName;
    private String texturePath;

    private Item item;

    private ItemBuilder(RegistryHelper<Item> registry, String name) {
        this.registry = registry;
        this.name = name;
        this.settings = new Item.Settings();
    }

    public static ItemBuilder register(RegistryHelper<Item> registry, String name) {
        return new ItemBuilder(registry, name);
    }

    public ItemBuilder settings(Item.Settings settings) {
        if (settings != null) {
            this.settings = settings;
        }
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder texture(String texturePath) {
        this.texturePath = texturePath;
        // Implement texture handling later if needed
        return this;
    }

    public Item build() {
        if (item == null) {
            item = new Item(settings);
            registry.register(name, item);

            ResourcePackDataGenerator.generateItemModel(name, GroovyEngine.MODID + ":item/" + name);

            // Register lang entry
            if (displayName != null) {
                LangGenerator.addLangEntry("item." + GroovyEngine.MODID + "." + name, displayName);
            } else {
                LangGenerator.addLangEntry("item." + GroovyEngine.MODID + "." + name, LangGenerator.toDisplayName(name));
            }
        }
        return item;
    }
}
