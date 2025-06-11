package io.github.luckymcdev;

import io.github.luckymcdev.api.editor.EditorState;
import io.github.luckymcdev.api.editor.MainEditorImGui;
import io.github.luckymcdev.api.input.keybinding.GroovyKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class GroovyEngineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GroovyKeybinds.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (GroovyKeybinds.openMainEditorKey.wasPressed()) {
                EditorState.open = !EditorState.open;
            }

        });

        HudRenderCallback.EVENT.register((drawContext, delta) -> {
            if (EditorState.open) {
                MainEditorImGui.render();
            }
        });

    }
}
