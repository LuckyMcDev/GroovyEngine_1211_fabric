package io.github.luckymcdev.groovyengine.gemodelling.core;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.editor.core.MainEditorImGui;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class GeModellingImGui {
    // Editor state
    private static boolean showTransformEditor = true;
    private static Vec3d position = Vec3d.ZERO;
    private static Vec3d rotation = Vec3d.ZERO; // Euler angles in degrees
    private static Vec3d scale = new Vec3d(1, 1, 1);
    private static DisplayType entityType = DisplayType.BLOCK;
    private static BlockState selectedBlock = Blocks.STONE.getDefaultState();
    private static ItemStack selectedItem = new ItemStack(Items.DIAMOND);

    // Entity tracking and selection
    private static final Set<Integer> trackedEntities = new HashSet<>();
    private static DisplayEntity selectedEntity = null;
    private static boolean autoSelectOnHit = true;
    private static Vec3d lastSelectedEntityPos = Vec3d.ZERO;

    private enum DisplayType {
        BLOCK, ITEM
    }

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

            if (hitEntity instanceof DisplayEntity displayEntity && hitEntity != selectedEntity) {
                selectEntity(displayEntity);
            }
        }
    }

    private static void selectEntity(DisplayEntity entity) {
        selectedEntity = entity;
        lastSelectedEntityPos = entity.getPos();

        // Update UI values to match selected entity
        AffineTransformation transform = entity.getTransformation(entity.getDataTracker());
        if (transform != null) {
            // Extract position offset (difference from entity position)
            position = Vec3d.ZERO; // Reset offset since we're now editing an existing entity

            // Extract scale
            Vector3f scaleVec = transform.getScale();
            scale = new Vec3d(scaleVec.x, scaleVec.y, scaleVec.z);

            // Extract rotation (convert quaternion back to Euler angles)
            Quaternionf rotQuat = transform.getLeftRotation(); // Use getLeftRotation() instead of getRotation()
            rotation = quaternionToEuler(rotQuat);
        }

        // Set entity type and block/item based on selected entity
        if (entity instanceof BlockDisplayEntity blockEntity) {
            entityType = DisplayType.BLOCK;
            selectedBlock = blockEntity.getBlockState();
        } else if (entity instanceof ItemDisplayEntity itemEntity) {
            entityType = DisplayType.ITEM;
            selectedItem = itemEntity.getItemStack();
        }

        GroovyEngine.LOGGER.info("Selected display entity: {} at position: {}",
                entity.getClass().getSimpleName(), entity.getPos());
    }

    private static Vec3d quaternionToEuler(Quaternionf quat) {
        // Convert quaternion to Euler angles (in degrees)
        // Using JOML's getEulerAnglesXYZ method if available, otherwise manual calculation
        Vector3f eulerAngles = new Vector3f();
        quat.getEulerAnglesXYZ(eulerAngles);

        return new Vec3d(Math.toDegrees(eulerAngles.x), Math.toDegrees(eulerAngles.y), Math.toDegrees(eulerAngles.z));
    }

    private static void renderTransformEditor() {
        ImGui.separator();
        ImGui.text("Display Entity Editor");

        // Show selected entity info
        if (selectedEntity != null && selectedEntity.isAlive()) {
            ImGui.text("Selected Entity: " + selectedEntity.getClass().getSimpleName() + " (ID: " + selectedEntity.getId() + ")");
            ImGui.text("Entity Position: " + String.format("%.2f, %.2f, %.2f",
                    selectedEntity.getPos().x, selectedEntity.getPos().y, selectedEntity.getPos().z));

            if (ImGui.button("Deselect Entity")) {
                selectedEntity = null;
                position = Vec3d.ZERO;
                rotation = Vec3d.ZERO;
                scale = new Vec3d(1, 1, 1);
            }
            ImGui.separator();
        } else if (selectedEntity != null) {
            ImGui.text("Selected entity is no longer valid");
            selectedEntity = null;
        }

        // Entity type selection (only for new entities)
        if (selectedEntity == null) {
            if (ImGui.radioButton("Block Display", entityType == DisplayType.BLOCK)) {
                entityType = DisplayType.BLOCK;
            }
            ImGui.sameLine();
            if (ImGui.radioButton("Item Display", entityType == DisplayType.ITEM)) {
                entityType = DisplayType.ITEM;
            }

            // Block/Item selection
            if (entityType == DisplayType.BLOCK) {
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
            float[] posArr = { (float)entityPos.x, (float)entityPos.y, (float)entityPos.z };
            if (ImGui.dragFloat3("Position", posArr, 0.05f)) {
                Vec3d newPos = new Vec3d(posArr[0], posArr[1], posArr[2]);
                updateSelectedEntityPosition(newPos);
            }
        } else {
            // For new entities, show offset from player
            float[] posArr = { (float)position.x, (float)position.y, (float)position.z };
            if (ImGui.dragFloat3("Position Offset", posArr, 0.05f)) {
                position = new Vec3d(posArr[0], posArr[1], posArr[2]);
            }
        }

        // Rotation controls
        float[] rotArr = { (float)rotation.x, (float)rotation.y, (float)rotation.z };
        if (ImGui.dragFloat3("Rotation", rotArr, 1.0f, -180, 180)) {
            rotation = new Vec3d(rotArr[0], rotArr[1], rotArr[2]);
            if (selectedEntity != null) {
                updateSelectedEntityTransform();
            }
        }

        // Scale controls
        float[] scaleArr = { (float)scale.x, (float)scale.y, (float)scale.z };
        if (ImGui.dragFloat3("Scale", scaleArr, 0.05f, 0.01f, 10f)) {
            scale = new Vec3d(scaleArr[0], scaleArr[1], scaleArr[2]);
            if (selectedEntity != null) {
                updateSelectedEntityTransform();
            }
        }

        // Action buttons
        if (selectedEntity == null) {
            if (ImGui.button("Spawn at Player")) {
                spawnEntityAtPlayer();
            }
        } else {
            if (ImGui.button("Delete Selected Entity")) {
                deleteSelectedEntity();
            }
        }
    }

    private static void updateSelectedEntityPosition(Vec3d newPos) {
        if (selectedEntity == null || !selectedEntity.isAlive()) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Update on server side if possible
        if (client.getServer() != null && client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey()) != null) {
            var serverWorld = client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey());
            Entity serverEntity = serverWorld.getEntityById(selectedEntity.getId());
            if (serverEntity instanceof DisplayEntity serverDisplayEntity) {
                serverDisplayEntity.setPosition(newPos);
            }
        }

        // Update client side
        selectedEntity.setPosition(newPos);
    }

    private static void updateSelectedEntityTransform() {
        if (selectedEntity == null || !selectedEntity.isAlive()) return;

        // Convert Euler angles to quaternion for rotation
        Quaternionf rotationQuat = new Quaternionf()
                .rotateXYZ(
                        (float)Math.toRadians(rotation.x),
                        (float)Math.toRadians(rotation.y),
                        (float)Math.toRadians(rotation.z)
                );

        // Create new transformation
        AffineTransformation transform = new AffineTransformation(
                new Vector3f(0, 0, 0), // No position offset in transformation
                rotationQuat,
                new Vector3f((float)scale.x, (float)scale.y, (float)scale.z),
                null
        );

        MinecraftClient client = MinecraftClient.getInstance();

        // Update on server side if possible
        if (client.getServer() != null && client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey()) != null) {
            var serverWorld = client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey());
            Entity serverEntity = serverWorld.getEntityById(selectedEntity.getId());
            if (serverEntity instanceof DisplayEntity serverDisplayEntity) {
                serverDisplayEntity.setTransformation(transform);
            }
        }

        // Update client side
        selectedEntity.setTransformation(transform);
    }

    private static void deleteSelectedEntity() {
        if (selectedEntity == null || !selectedEntity.isAlive()) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Remove on server side if possible
        if (client.getServer() != null && client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey()) != null) {
            var serverWorld = client.getServer().getWorld(selectedEntity.getWorld().getRegistryKey());
            Entity serverEntity = serverWorld.getEntityById(selectedEntity.getId());
            if (serverEntity != null) {
                serverEntity.discard();
            }
        }

        // Remove client side
        selectedEntity.discard();
        trackedEntities.remove(selectedEntity.getId());

        GroovyEngine.LOGGER.info("Deleted display entity: {}", selectedEntity.getId());
        selectedEntity = null;
    }

    private static void renderBlockSelector() {
        // Simple block selector
        String currentBlockName = Registries.BLOCK.getId(selectedBlock.getBlock()).toString();
        if (ImGui.beginCombo("Block", currentBlockName)) {
            for (Block block : Registries.BLOCK) {
                if (ImGui.selectable(Registries.BLOCK.getId(block).toString(),
                        block == selectedBlock.getBlock())) {
                    selectedBlock = block.getDefaultState();
                    GroovyEngine.LOGGER.info("Selected block changed to: {}", currentBlockName);
                }
            }
            ImGui.endCombo();
        }
    }

    private static void renderItemSelector() {
        // Simple item selector
        String currentItemName = Registries.ITEM.getId(selectedItem.getItem()).toString();
        if (ImGui.beginCombo("Item", currentItemName)) {
            for (Item item : Registries.ITEM) {
                if (ImGui.selectable(Registries.ITEM.getId(item).toString(),
                        item == selectedItem.getItem())) {
                    selectedItem = new ItemStack(item);
                    GroovyEngine.LOGGER.info("Selected item changed to: {}", currentItemName);
                }
            }
            ImGui.endCombo();
        }
    }

    private static void spawnEntityAtPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            GroovyEngine.LOGGER.error("Failed to spawn entity: Player is null");
            return;
        }

        if (client.world == null) {
            GroovyEngine.LOGGER.error("Failed to spawn entity: World is null");
            return;
        }

        // Get player position and add offset
        Vec3d playerPos = client.player.getPos();
        Vec3d spawnPos = playerPos.add(position);

        GroovyEngine.LOGGER.info("Attempting to spawn {} at position: {} (Player pos: {}, Offset: {})",
                entityType, spawnPos, playerPos, position);

        // Convert Euler angles to quaternion for rotation
        Quaternionf rotationQuat = new Quaternionf()
                .rotateXYZ(
                        (float)Math.toRadians(rotation.x),
                        (float)Math.toRadians(rotation.y),
                        (float)Math.toRadians(rotation.z)
                );

        // Create AffineTransformation with RELATIVE transformation (not world position)
        AffineTransformation transform = new AffineTransformation(
                new Vector3f(0, 0, 0), // No position offset in transformation
                rotationQuat,
                new Vector3f((float)scale.x, (float)scale.y, (float)scale.z),
                null
        );

        GroovyEngine.LOGGER.debug("Created transformation: {}", transform);

        // Create and spawn entity
        DisplayEntity spawnedEntity;
        if (entityType == DisplayType.BLOCK) {
            spawnedEntity = spawnBlockDisplay(selectedBlock, spawnPos, transform);
        } else {
            spawnedEntity = spawnItemDisplay(selectedItem, spawnPos, transform);
        }

        // Track the spawned entity and auto-select it
        if (spawnedEntity != null) {
            trackedEntities.add(spawnedEntity.getId());
            selectedEntity = spawnedEntity;
        }
    }

    private static BlockDisplayEntity spawnBlockDisplay(BlockState block, Vec3d worldPos, AffineTransformation transform) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            GroovyEngine.LOGGER.error("Cannot spawn block display - world is null");
            return null;
        }

        GroovyEngine.LOGGER.info("Creating block display with block: {}",
                Registries.BLOCK.getId(block.getBlock()));

        BlockDisplayEntity entity = null;

        // IMPORTANT: Check if we're on integrated server (singleplayer)
        // and spawn on server side if possible
        if (client.getServer() != null && client.getServer().getWorld(client.world.getRegistryKey()) != null) {
            // We're in singleplayer, spawn on server side
            var serverWorld = client.getServer().getWorld(client.world.getRegistryKey());

            BlockDisplayEntity serverEntity = new BlockDisplayEntity(
                    EntityType.BLOCK_DISPLAY,
                    serverWorld
            );

            serverEntity.setPosition(worldPos);
            serverEntity.setBlockState(block);
            serverEntity.setTransformation(transform);

            serverWorld.spawnEntity(serverEntity);
            entity = serverEntity; // Return server entity as it's the "real" one
            GroovyEngine.LOGGER.info("Block display spawned on server side! Entity ID: {}", serverEntity.getId());
        } else {
            // Multiplayer - would need packet to send to server
            // For now, spawn client-side (will be temporary)
            entity = new BlockDisplayEntity(EntityType.BLOCK_DISPLAY, client.world);
            entity.setPosition(worldPos);
            entity.setBlockState(block);
            entity.setTransformation(transform);

            client.world.spawnEntity(entity);
            GroovyEngine.LOGGER.info("Block display spawned client-side (temporary)! Entity ID: {}", entity.getId());
            GroovyEngine.LOGGER.warn("Client-side entity spawn - will not persist or be visible to other players!");
        }

        return entity;
    }

    private static ItemDisplayEntity spawnItemDisplay(ItemStack item, Vec3d worldPos, AffineTransformation transform) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            GroovyEngine.LOGGER.error("Cannot spawn item display - world is null");
            return null;
        }

        GroovyEngine.LOGGER.info("Creating item display with item: {}",
                Registries.ITEM.getId(item.getItem()));

        ItemDisplayEntity entity = null;

        // IMPORTANT: Check if we're on integrated server (singleplayer)
        if (client.getServer() != null && client.getServer().getWorld(client.world.getRegistryKey()) != null) {
            // We're in singleplayer, spawn on server side
            var serverWorld = client.getServer().getWorld(client.world.getRegistryKey());

            ItemDisplayEntity serverEntity = new ItemDisplayEntity(
                    EntityType.ITEM_DISPLAY,
                    serverWorld
            );

            serverEntity.setPosition(worldPos);
            serverEntity.setItemStack(item);
            serverEntity.setTransformation(transform);

            assert serverWorld != null;
            serverWorld.spawnEntity(serverEntity);
            entity = serverEntity; // Return server entity as it's the "real" one

            GroovyEngine.LOGGER.info("Item display spawned on server side! Entity ID: {}", serverEntity.getId());
        } else {
            // Multiplayer - would need packet to send to server
            entity = new ItemDisplayEntity(EntityType.ITEM_DISPLAY, client.world);
            entity.setPosition(worldPos);
            entity.setItemStack(item);
            entity.setTransformation(transform);

            client.world.spawnEntity(entity);
            GroovyEngine.LOGGER.info("Item display spawned client-side (temporary)! Entity ID: {}", entity.getId());
            GroovyEngine.LOGGER.warn("Client-side entity spawn - will not persist or be visible to other players!");
        }

        return entity;
    }
}