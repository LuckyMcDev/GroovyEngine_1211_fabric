package io.github.luckymcdev.groovyengine.scripting.events;

import groovy.lang.Closure;
import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class GuiEvents {

    public static void onScreenInit(Closure<Void> closure) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            EventContext ctx = new EventContext("screen_init")
                    .withClient(client)
                    .withScreen(screen)
                    .withScreenSize(scaledWidth, scaledHeight);
            closure.call(ctx);
        });
    }

    public static void onTooltip(Closure<Void> closure) {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            EventContext ctx = new EventContext("item_tooltip")
                    .withItemStack(stack)
                    .withTooltipContext(tooltipContext)
                    .withTooltipType(tooltipType)
                    .withTooltipLines(lines);
            closure.call(ctx);
        });
    }
}