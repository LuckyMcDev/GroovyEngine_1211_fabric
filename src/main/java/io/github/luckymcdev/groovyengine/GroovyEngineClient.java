package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.editor.core.MainEditorImGui;
import io.github.luckymcdev.groovyengine.editor.core.MainEditorScreen;
import io.github.luckymcdev.groovyengine.input.GroovyKeybinds;
import io.github.luckymcdev.groovyengine.rendering.core.GroovyRenderList;
import io.github.luckymcdev.groovyengine.rendering.core.GroovyRenderer;
import io.github.luckymcdev.groovyengine.rendering.instances.RenderBlockInstance;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
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

                assert MinecraftClient.getInstance().player != null;
                Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
                Vec3d pos = playerPos.add(2, 2, 0); // Render 2 blocks above player
                GroovyRenderList.addBlock(new RenderBlockInstance(Blocks.DIAMOND_BLOCK.getDefaultState(), pos));
            }

        });

        GroovyRenderer.init();


        GroovyRenderList.clearAll();

        GroovyRenderList.addBlock(new RenderBlockInstance(Blocks.DIAMOND_BLOCK.getDefaultState(), new Vec3d(0, 2, 0)));
        GroovyRenderList.addBlock(new RenderBlockInstance(Blocks.GOLD_BLOCK.getDefaultState(), new Vec3d(5, 2, 0)));
        GroovyRenderList.addBlock(new RenderBlockInstance(Blocks.IRON_BLOCK.getDefaultState(), new Vec3d(-5, 2, 0)));

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MainEditorImGui.render();
        });

        // Init Client Events
        EventRegistry.initClient();
    }
}
