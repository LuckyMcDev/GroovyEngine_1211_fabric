package io.github.luckymcdev.groovyengine.mixin.command;

import io.github.luckymcdev.groovyengine.script_event.Events;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadCommand.class)
public class MixinReloadCommand {

    /**
     * Injects into the execution of the /reload command.
     * This specific injection point (`@At("RETURN")`) means your code will run
     * AFTER Minecraft has completed its internal reload process (e.g., data packs loaded).
     *
     * If you wanted to run code *before* the reload starts, you would change `@At("RETURN")` to `@At("HEAD")`.
     *
     * @param source The source of the command (e.g., a player, the console).
     * @param cir The CallbackInfoReturnable, as the execute method returns an int.
     */
    @Inject(method = "execute", at = @At("RETURN"))
    private static void groovyengine_onReloadCommandExecuted(ServerCommandSource source, CallbackInfoReturnable<Integer> cir) {
        System.out.println("GroovyEngine: /reload command detected and handled.");
        Events.clear();
        GroovyScriptManager.loadScripts();
    }
}