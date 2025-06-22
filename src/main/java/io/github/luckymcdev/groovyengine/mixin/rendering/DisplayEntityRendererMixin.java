package io.github.luckymcdev.groovyengine.mixin.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.DisplayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Second Mixin: Modify display entity rendering to disable fog and depth testing
@Mixin(DisplayEntityRenderer.class)
abstract class DisplayEntityRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/DisplayEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void preRender(DisplayEntity displayEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        // Push current GL state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Disable depth testing so it renders on top of everything
        RenderSystem.disableDepthTest();

        // Disable fog completely
        RenderSystem.setShaderFogStart(Float.MAX_VALUE);
        RenderSystem.setShaderFogEnd(Float.MAX_VALUE);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/DisplayEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("RETURN")
    )
    private void postRender(DisplayEntity displayEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        // Restore GL state
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        // Reset fog to normal values (these might need adjustment based on your game settings)
        RenderSystem.setShaderFogStart(0.0f);
        RenderSystem.setShaderFogEnd(1000.0f);
    }
}
