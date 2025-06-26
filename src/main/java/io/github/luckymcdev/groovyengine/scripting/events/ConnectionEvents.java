package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ConnectionEvents {
    public static void onClientJoin(ClientPlayConnectionEvents.Join callback) {
        ClientPlayConnectionEvents.JOIN.register(callback);
    }

    public static void onClientDisconnect(ClientPlayConnectionEvents.Disconnect callback) {
        ClientPlayConnectionEvents.DISCONNECT.register(callback);
    }

    public static void onServerJoin(ServerPlayConnectionEvents.Join callback) {
        ServerPlayConnectionEvents.JOIN.register(callback);
    }

    public static void onServerDisconnect(ServerPlayConnectionEvents.Disconnect callback) {
        ServerPlayConnectionEvents.DISCONNECT.register(callback);
    }
}
