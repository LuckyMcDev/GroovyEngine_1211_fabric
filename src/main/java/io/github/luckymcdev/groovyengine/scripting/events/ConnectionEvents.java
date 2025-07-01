package io.github.luckymcdev.groovyengine.scripting.events;

import groovy.lang.Closure;
import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ConnectionEvents {

    public static void onClientJoin(Closure<Void> closure) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            EventContext ctx = new EventContext("client_join")
                    .withClientPlayNetworkHandler(handler)
                    .withPacketSender(sender)
                    .withClient(client);
            closure.call(ctx);
        });
    }

    public static void onClientDisconnect(Closure<Void> closure) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            EventContext ctx = new EventContext("client_disconnect")
                    .withClientPlayNetworkHandler(handler)
                    .withClient(client);
            closure.call(ctx);
        });
    }

    public static void onServerJoin(Closure<Void> closure) {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EventContext ctx = new EventContext("server_join")
                    .withServerPlayer(handler.getPlayer())
                    .withPacketSender(sender)
                    .withServer(server);
            closure.call(ctx);
        });
    }

    public static void onServerDisconnect(Closure<Void> closure) {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            EventContext ctx = new EventContext("server_disconnect")
                    .withServerPlayer(handler.getPlayer())
                    .withServer(server);
            closure.call(ctx);
        });
    }
}