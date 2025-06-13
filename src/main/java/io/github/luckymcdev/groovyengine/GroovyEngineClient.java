package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.editor.gui.MainEditorImGui;
import io.github.luckymcdev.groovyengine.editor.gui.MainEditorScreen;
import io.github.luckymcdev.groovyengine.input.GroovyKeybinds;
import io.github.luckymcdev.groovyengine.event.EventRegistry;
import io.github.luckymcdev.groovyengine.packloading.command.GroovyPackClientCommand;
import io.github.luckymcdev.groovyengine.packloading.command.PackClientCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Environment(EnvType.CLIENT)
public class GroovyEngineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GroovyKeybinds.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (GroovyKeybinds.openMainEditorKey.wasPressed()) {
                client.setScreen(new MainEditorScreen(Text.of("Main Editor")));
            }

        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MainEditorImGui.render();
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) ->
                dispatcher.register(createCommand(new GroovyPackClientCommand())) // Renamed
        );

        EventRegistry.initClient();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> createCommand(PackClientCommand command) { // Type updated
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal(command.id());
        if (!command.isHidden()) {
            builder = builder.executes(context -> command.execute());
        }
        for (PackClientCommand subCommand : command.commands()) { // Type updated
            if (subCommand.isHidden()) continue;
            builder = builder.then(createCommand(subCommand));
        }
        return builder;
    }
}
