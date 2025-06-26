package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class WorldEvents {
    public static void onLoad(ServerWorldEvents.Load callback) {
        ServerWorldEvents.LOAD.register(callback);
    }

    public static void onUnload(ServerWorldEvents.Unload callback) {
        ServerWorldEvents.UNLOAD.register(callback);
    }
}
