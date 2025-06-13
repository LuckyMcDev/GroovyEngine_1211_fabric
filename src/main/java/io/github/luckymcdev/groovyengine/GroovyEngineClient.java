package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.editor.gui.MainEditorImGui;
import io.github.luckymcdev.groovyengine.editor.gui.MainEditorScreen;
import io.github.luckymcdev.groovyengine.input.GroovyKeybinds;
import io.github.luckymcdev.groovyengine.event.EventRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;

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

        EventRegistry.initClient();
    }
}
