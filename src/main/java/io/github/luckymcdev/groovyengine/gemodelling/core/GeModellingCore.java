package io.github.luckymcdev.groovyengine.gemodelling.core;

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

import java.util.HashSet;
import java.util.Set;

public class GeModellingCore {
    // Entity tracking and selection
    private static final Set<Integer> trackedEntities = new HashSet<>();
    private static DisplayEntity selectedEntity = null;
    private static Vec3d lastSelectedEntityPos = Vec3d.ZERO;

    // Editor state
    private static Vec3d position = Vec3d.ZERO;
    private static Vec3d rotation = Vec3d.ZERO; // Euler angles in degrees
    private static Vec3d scale = new Vec3d(1, 1, 1);
    private static DisplayType entityType = DisplayType.BLOCK;
    private static BlockState selectedBlock = Blocks.STONE.getDefaultState();
    private static ItemStack selectedItem = new ItemStack(Items.DIAMOND);

    public enum DisplayType {
        BLOCK, ITEM
    }

    public static void selectEntity(DisplayEntity entity) {
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
    }

    public static Vec3d quaternionToEuler(Quaternionf quat) {
        // Convert quaternion to Euler angles (in degrees)
        Vector3f eulerAngles = new Vector3f();
        quat.getEulerAnglesXYZ(eulerAngles);
        return new Vec3d(Math.toDegrees(eulerAngles.x), Math.toDegrees(eulerAngles.y), Math.toDegrees(eulerAngles.z));
    }

    public static void updateSelectedEntityPosition(Vec3d newPos) {
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

    public static void updateSelectedEntityTransform() {
        if (selectedEntity == null || !selectedEntity.isAlive()) return;
        // Convert Euler angles to quaternion for rotation
        Quaternionf rotationQuat = new Quaternionf()
                .rotateXYZ(
                        (float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z)
                );
        // Create new transformation
        AffineTransformation transform = new AffineTransformation(
                new Vector3f(0, 0, 0), // No position offset in transformation
                rotationQuat,
                new Vector3f((float) scale.x, (float) scale.y, (float) scale.z),
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

    public static void deleteSelectedEntity() {
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
        selectedEntity = null;
    }

    public static void spawnEntityAtPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }

        // Get player position and add offset
        Vec3d playerPos = client.player.getPos();
        Vec3d spawnPos = playerPos.add(position);

        // Convert Euler angles to quaternion for rotation
        Quaternionf rotationQuat = new Quaternionf()
                .rotateXYZ(
                        (float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z)
                );

        // Create AffineTransformation with RELATIVE transformation (not world position)
        AffineTransformation transform = new AffineTransformation(
                new Vector3f(0, 0, 0), // No position offset in transformation
                rotationQuat,
                new Vector3f((float) scale.x, (float) scale.y, (float) scale.z),
                null
        );

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
            return null;
        }

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
        } else {
            // Multiplayer - would need packet to send to server
            // For now, spawn client-side (will be temporary)
            entity = new BlockDisplayEntity(EntityType.BLOCK_DISPLAY, client.world);
            entity.setPosition(worldPos);
            entity.setBlockState(block);
            entity.setTransformation(transform);
            client.world.spawnEntity(entity);
        }
        return entity;
    }

    private static ItemDisplayEntity spawnItemDisplay(ItemStack item, Vec3d worldPos, AffineTransformation transform) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) {
            return null;
        }

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
            serverWorld.spawnEntity(serverEntity);
            entity = serverEntity; // Return server entity as it's the "real" one
        } else {
            // Multiplayer - would need packet to send to server
            entity = new ItemDisplayEntity(EntityType.ITEM_DISPLAY, client.world);
            entity.setPosition(worldPos);
            entity.setItemStack(item);
            entity.setTransformation(transform);
            client.world.spawnEntity(entity);
        }
        return entity;
    }

    // Getters and Setters
    public static DisplayEntity getSelectedEntity() {
        return selectedEntity;
    }

    public static Vec3d getPosition() {
        return position;
    }

    public static void setPosition(Vec3d position) {
        GeModellingCore.position = position;
    }

    public static Vec3d getRotation() {
        return rotation;
    }

    public static void setRotation(Vec3d rotation) {
        GeModellingCore.rotation = rotation;
    }

    public static Vec3d getScale() {
        return scale;
    }

    public static void setScale(Vec3d scale) {
        GeModellingCore.scale = scale;
    }

    public static DisplayType getEntityType() {
        return entityType;
    }

    public static void setEntityType(DisplayType entityType) {
        GeModellingCore.entityType = entityType;
    }

    public static BlockState getSelectedBlock() {
        return selectedBlock;
    }

    public static void setSelectedBlock(BlockState selectedBlock) {
        GeModellingCore.selectedBlock = selectedBlock;
    }

    public static ItemStack getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(ItemStack selectedItem) {
        GeModellingCore.selectedItem = selectedItem;
    }
}
