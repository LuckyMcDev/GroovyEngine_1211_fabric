package io.github.luckymcdev.groovyengine.gemodelling.core;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.editor.core.MainEditorImGui;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class GeModellingImGui {
    private static boolean showTransformEditor = true;
    private static boolean autoSelectOnHit = true;

    public static void render() {
        ImGui.begin("GroovyEngine Modelling", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("This is the GroovyEngine Modelling UI group.");

        // Toggle for the transform editor
        ImGui.checkbox("Show Transform Editor", showTransformEditor);

        // Auto-select on hit toggle
        ImGui.checkbox("Auto-select on hit", autoSelectOnHit);

        // Check for entity selection via crosshair
        checkForEntitySelection();

        if (showTransformEditor) {
            renderTransformEditor();
        }

        if (ImGui.button("Close Modelling")) {
            MainEditorImGui.showGroovyEngineModellingGroup = false;
        }

        ImGui.end();
    }

    private static void checkForEntitySelection() {
        if (!autoSelectOnHit) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
            Entity hitEntity = entityHit.getEntity();
            if (hitEntity instanceof DisplayEntity displayEntity && hitEntity != GeModellingCore.getSelectedEntity()) {
                GeModellingCore.selectEntity(displayEntity);
            }
        }
    }

    private static void renderTransformEditor() {
        ImGui.separator();
        ImGui.text("Display Entity Editor");

        // Show selected entity info
        DisplayEntity selectedEntity = GeModellingCore.getSelectedEntity();
        if (selectedEntity != null && selectedEntity.isAlive()) {
            ImGui.text("Selected Entity: " + selectedEntity.getClass().getSimpleName() + " (ID: " + selectedEntity.getId() + ")");
            ImGui.text("Entity Position: " + String.format("%.2f, %.2f, %.2f",
                    selectedEntity.getPos().x, selectedEntity.getPos().y, selectedEntity.getPos().z));
            if (ImGui.button("Deselect Entity")) {
                GeModellingCore.selectEntity(null);
                GeModellingCore.setPosition(Vec3d.ZERO);
                GeModellingCore.setRotation(Vec3d.ZERO);
                GeModellingCore.setScale(new Vec3d(1, 1, 1));
            }
            ImGui.separator();
        } else if (selectedEntity != null) {
            ImGui.text("Selected entity is no longer valid");
            GeModellingCore.selectEntity(null);
        }

        // Entity type selection (only for new entities)
        if (selectedEntity == null) {
            if (ImGui.radioButton("Block Display", GeModellingCore.getEntityType() == GeModellingCore.DisplayType.BLOCK)) {
                GeModellingCore.setEntityType(GeModellingCore.DisplayType.BLOCK);
            }
            ImGui.sameLine();
            if (ImGui.radioButton("Item Display", GeModellingCore.getEntityType() == GeModellingCore.DisplayType.ITEM)) {
                GeModellingCore.setEntityType(GeModellingCore.DisplayType.ITEM);
            }

            // Block/Item selection
            if (GeModellingCore.getEntityType() == GeModellingCore.DisplayType.BLOCK) {
                renderBlockSelector();
            } else {
                renderItemSelector();
            }
        } else {
            // Show current block/item for selected entity
            if (selectedEntity instanceof BlockDisplayEntity blockEntity) {
                ImGui.text("Block: " + Registries.BLOCK.getId(blockEntity.getBlockState().getBlock()).toString());
            } else if (selectedEntity instanceof ItemDisplayEntity itemEntity) {
                ImGui.text("Item: " + Registries.ITEM.getId(itemEntity.getItemStack().getItem()).toString());
            }
        }

        // Transformation controls
        ImGui.separator();
        ImGui.text("Transformations");

        // Position controls (different behavior for selected vs new entities)
        if (selectedEntity != null) {
            // For selected entities, show absolute position
            Vec3d entityPos = selectedEntity.getPos();
            float[] posArr = { (float) entityPos.x, (float) entityPos.y, (float) entityPos.z };
            if (ImGui.dragFloat3("Position", posArr, 0.05f)) {
                Vec3d newPos = new Vec3d(posArr[0], posArr[1], posArr[2]);
                GeModellingCore.updateSelectedEntityPosition(newPos);
            }
        } else {
            // For new entities, show offset from player
            Vec3d position = GeModellingCore.getPosition();
            float[] posArr = { (float) position.x, (float) position.y, (float) position.z };
            if (ImGui.dragFloat3("Position Offset", posArr, 0.05f)) {
                GeModellingCore.setPosition(new Vec3d(posArr[0], posArr[1], posArr[2]));
            }
        }

        // Rotation controls
        Vec3d rotation = GeModellingCore.getRotation();
        float[] rotArr = { (float) rotation.x, (float) rotation.y, (float) rotation.z };
        if (ImGui.dragFloat3("Rotation", rotArr, 1.0f, -180, 180)) {
            GeModellingCore.setRotation(new Vec3d(rotArr[0], rotArr[1], rotArr[2]));
            if (selectedEntity != null) {
                GeModellingCore.updateSelectedEntityTransform();
            }
        }

        // Scale controls
        Vec3d scale = GeModellingCore.getScale();
        float[] scaleArr = { (float) scale.x, (float) scale.y, (float) scale.z };
        if (ImGui.dragFloat3("Scale", scaleArr, 0.05f, 0.01f, 10f)) {
            GeModellingCore.setScale(new Vec3d(scaleArr[0], scaleArr[1], scaleArr[2]));
            if (selectedEntity != null) {
                GeModellingCore.updateSelectedEntityTransform();
            }
        }

        // Action buttons
        if (selectedEntity == null) {
            if (ImGui.button("Spawn at Player")) {
                GeModellingCore.spawnEntityAtPlayer();
            }
        } else {
            if (ImGui.button("Delete Selected Entity")) {
                GeModellingCore.deleteSelectedEntity();
            }
        }
    }

    private static void renderBlockSelector() {
        // Simple block selector
        String currentBlockName = Registries.BLOCK.getId(GeModellingCore.getSelectedBlock().getBlock()).toString();
        if (ImGui.beginCombo("Block", currentBlockName)) {
            for (Block block : Registries.BLOCK) {
                if (ImGui.selectable(Registries.BLOCK.getId(block).toString(),
                        block == GeModellingCore.getSelectedBlock().getBlock())) {
                    GeModellingCore.setSelectedBlock(block.getDefaultState());
                    GroovyEngine.LOGGER.info("Selected block changed to: {}", currentBlockName);
                }
            }
            ImGui.endCombo();
        }
    }

    private static void renderItemSelector() {
        // Simple item selector
        String currentItemName = Registries.ITEM.getId(GeModellingCore.getSelectedItem().getItem()).toString();
        if (ImGui.beginCombo("Item", currentItemName)) {
            for (Item item : Registries.ITEM) {
                if (ImGui.selectable(Registries.ITEM.getId(item).toString(),
                        item == GeModellingCore.getSelectedItem().getItem())) {
                    GeModellingCore.setSelectedItem(new ItemStack(item));
                    GroovyEngine.LOGGER.info("Selected item changed to: {}", currentItemName);
                }
            }
            ImGui.endCombo();
        }
    }
}
