package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class LootEvents {
    public static void onModify(LootTableEvents.Modify callback) {
        LootTableEvents.MODIFY.register(callback);
    }
}
