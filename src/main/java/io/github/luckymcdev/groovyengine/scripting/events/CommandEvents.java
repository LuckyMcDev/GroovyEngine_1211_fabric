package io.github.luckymcdev.groovyengine.scripting.events;

import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import groovy.lang.Closure;

public class CommandEvents {

    public static void onRegister(Closure<Void> closure) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            EventContext ctx = new EventContext("command_register")
                    .withCommandDispatcher(dispatcher)
                    .withCommandRegistryAccess(registryAccess)
                    .withCommandEnvironment(environment);
            closure.call(ctx);
        });
    }
}