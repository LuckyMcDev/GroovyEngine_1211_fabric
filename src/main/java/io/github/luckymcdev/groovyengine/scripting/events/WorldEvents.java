package io.github.luckymcdev.groovyengine.scripting.events;

import groovy.lang.Closure;
import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class WorldEvents {

    public static void onLoad(Closure<Void> closure) {
        ServerWorldEvents.LOAD.register((server, world) -> {
            EventContext ctx = new EventContext("world_load")
                    .withServer(server)
                    .withServerWorld(world);
            closure.call(ctx);
        });
    }

    public static void onUnload(Closure<Void> closure) {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            EventContext ctx = new EventContext("world_unload")
                    .withServer(server)
                    .withServerWorld(world);
            closure.call(ctx);
        });
    }
}