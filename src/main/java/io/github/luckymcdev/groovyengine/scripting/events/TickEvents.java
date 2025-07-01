package io.github.luckymcdev.groovyengine.scripting.events;

import groovy.lang.Closure;
import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickEvents {

    public static void onStartClientTick(Closure<Void> closure) {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            EventContext ctx = new EventContext("client_tick_start")
                    .withClient(client);
            closure.call(ctx);
        });
    }

    public static void onEndClientTick(Closure<Void> closure) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EventContext ctx = new EventContext("client_tick_end")
                    .withClient(client);
            closure.call(ctx);
        });
    }

    public static void onStartServerTick(Closure<Void> closure) {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            EventContext ctx = new EventContext("server_tick_start")
                    .withServer(server);
            closure.call(ctx);
        });
    }

    public static void onEndServerTick(Closure<Void> closure) {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            EventContext ctx = new EventContext("server_tick_end")
                    .withServer(server);
            closure.call(ctx);
        });
    }
}