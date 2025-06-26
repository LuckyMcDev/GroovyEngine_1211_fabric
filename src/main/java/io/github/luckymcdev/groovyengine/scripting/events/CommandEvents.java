package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandEvents {
    public static void onRegister(CommandRegistrationCallback callback) {
        CommandRegistrationCallback.EVENT.register(callback);
    }
}
