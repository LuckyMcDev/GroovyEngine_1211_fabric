package io.github.luckymcdev.groovyengine.rendering.instances;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class RenderItemInstance {
    private final ItemStack itemStack;
    private Vec3d position;
    private Vec3d rotation;
    private Vec3d scale;

    public RenderItemInstance(ItemStack stack, Vec3d pos) {
        this.itemStack = stack;
        this.position = pos;
        this.rotation = Vec3d.ZERO;
        this.scale = new Vec3d(1, 1, 1);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Translate to absolute world position
        matrices.translate(position.x, position.y, position.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) rotation.y));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rotation.x));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation.z));

        matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

        MinecraftClient.getInstance().getItemRenderer()
                .renderItem(itemStack, ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV,
                        matrices, vertexConsumers, null, 0);

        matrices.pop();
    }

    public void setPosition(Vec3d pos) { this.position = pos; }
    public void setRotation(Vec3d rot) { this.rotation = rot; }
    public void setScale(Vec3d scale) { this.scale = scale; }

    // Getter for position (useful for debugging)
    public Vec3d getPosition() { return this.position; }
}