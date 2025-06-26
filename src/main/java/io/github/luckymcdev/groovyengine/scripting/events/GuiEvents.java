package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class GuiEvents {
    public static void onScreenInit(ScreenEvents.AfterInit callback) {
        ScreenEvents.AFTER_INIT.register(callback);
    }

    public static void onTooltip(ItemTooltipCallback callback) {
        ItemTooltipCallback.EVENT.register(callback);
    }
}
