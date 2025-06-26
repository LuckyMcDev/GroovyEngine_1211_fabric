package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickEvents {

    // Client Tick Start
    public static void onStartClientTick(groovy.lang.Closure<?> closure) {
        ClientTickEvents.START_CLIENT_TICK.register(closure::call);
    }

    // Client Tick End
    public static void onEndClientTick(groovy.lang.Closure<?> closure) {
        ClientTickEvents.END_CLIENT_TICK.register(closure::call);
    }

    // Server Tick Start
    public static void onStartServerTick(groovy.lang.Closure<?> closure) {
        ServerTickEvents.START_SERVER_TICK.register(closure::call);
    }

    // Server Tick End
    public static void onEndServerTick(groovy.lang.Closure<?> closure) {
        ServerTickEvents.END_SERVER_TICK.register(closure::call);
    }
}
