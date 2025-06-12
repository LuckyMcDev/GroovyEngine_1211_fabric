package io.github.luckymcdev.api.scripting;

import net.minecraft.item.Item;

public class GroovyEngineContext {

    public static Item.Settings createItemSettings() {
        return new Item.Settings();
    }
}