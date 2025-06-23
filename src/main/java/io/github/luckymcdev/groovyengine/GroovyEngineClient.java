package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.editor.core.MainEditorImGui;
import io.github.luckymcdev.groovyengine.editor.core.MainEditorScreen;
import io.github.luckymcdev.groovyengine.input.GroovyKeybinds;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventContext;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventRegistry;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.GroovyRegisterBlockEvents;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.GroovyRegisterItemEvents;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.GroovyShaderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

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
        fireRegisterShaderEvent();


    }

    private static void fireRegisterShaderEvent() {
        EventContext ctx = new EventContext("registerShader");
        GroovyShaderEvents.fire(ctx);
    }
}
