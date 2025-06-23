package io.github.luckymcdev.groovyengine.rendering.core;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.rendering.instances.RenderBlockInstance;
import io.github.luckymcdev.groovyengine.rendering.instances.RenderItemInstance;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.Vec3d;

public class GroovyRenderer {
    public static void init() {
        WorldRenderEvents.LAST.register(context -> {
            if (GroovyRenderList.blocks.isEmpty() && GroovyRenderList.items.isEmpty()) {
                return;
            }

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider vertexConsumers = context.consumers();
            int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

            // Get camera position for relative rendering
            Vec3d cameraPos = context.camera().getPos();

            // Check if matrices is null and handle appropriately
            if (matrices == null) {
                matrices = new MatrixStack();
            }

            matrices.push();

            // Translate by negative camera position to make world coordinates work
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            for (RenderBlockInstance block : GroovyRenderList.blocks) {
                block.render(matrices, vertexConsumers, light);
            }

            for (RenderItemInstance item : GroovyRenderList.items) {
                item.render(matrices, vertexConsumers, light);
            }

            matrices.pop();
        });
    }
}