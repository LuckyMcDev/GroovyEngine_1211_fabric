package io.github.luckymcdev.api.scripting.registry;

import io.github.luckymcdev.util.RegistryHelper;
import net.minecraft.item.Item;

public class ItemRegistrar {
    private final RegistryHelper<Item> registry;

    public ItemRegistrar(RegistryHelper<Item> registry) {
        this.registry = registry;
    }

    public Item register(String name) {
        return register(name, new Item.Settings());
    }

    public Item register(String name, Item.Settings settings) {
        Item item = new Item(settings);
        return registry.register(name, item);
    }
}


