package io.github.luckymcdev.groovyengine.scripting.eventservice;

import io.github.luckymcdev.groovyengine.scripting.eventservice.events.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents; // For entity death
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity; // For entity death events
import net.minecraft.entity.damage.DamageSource; // For entity death events
import net.minecraft.world.World;

public class EventRegistry {

    public static void initServer() {
        // Clear all listeners from previous reloads/initializations to prevent duplicates
        clearAllEvents();

        registerBlockBreak();
        registerBlockPlace();
        registerPlayerJoin();
        registerPlayerQuit();
        registerServerTick();
        registerPlayerInteractItem(); // New event
        registerEntityDeath(); // New event

        // Consider if "registerItem" and "registerBlock" should be actual events or direct API calls.
        // As events that fire on init, they're handled here:
        fireRegisterItemEvent();
        fireRegisterBlockEvent();
    }

    public static void initClient() {
        // Clear all listeners from previous reloads/initializations to prevent duplicates
        clearAllEvents();

        registerClientTick();
        // Consider if "registerItem" and "registerBlock" should be actual events or direct API calls.
        fireRegisterItemEvent();
        fireRegisterBlockEvent();
    }

    /**
     * Clears all listeners from all custom event classes.
     * Call this before initializing events (e.g., on server start or data pack reload)
     * to prevent listeners from being registered multiple times.
     */
    public static void clearAllEvents() {
        GroovyClientTickEvents.clear();
        GroovyServerTickEvents.clear();
        GroovyBlockBreakEvents.clear();
        GroovyBlockPlaceEvents.clear();
        GroovyPlayerJoinEvents.clear();
        GroovyPlayerLeaveEvents.clear();
        GroovyPlayerInteractItemEvents.clear();
        GroovyEntityDeathEvents.clear();
        // Add all your new event classes here
    }

    // --- SERVER EVENTS ---

    private static void registerBlockBreak() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClient()) { // Ensure it's server-side
                BlockState state = world.getBlockState(pos);
                EventContext ctx = new EventContext("blockBreak")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world) // Cast to ServerWorld is safe on server
                        .withPos(pos)
                        .withBlock(state)
                        .withHand(hand); // Add hand context
                GroovyBlockBreakEvents.fire(ctx); // Fire to the specific event class
            }
            return ActionResult.PASS;
        });
    }

    private static void registerBlockPlace() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // Check for !world.isClient() and !player.isSneaking() with Hand.MAIN_HAND to prevent double-firing
            // or accidental client-side calls depending on your needs.
            // Simplified for example:
            if (!world.isClient()) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                EventContext ctx = new EventContext("blockPlace")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withPos(pos)
                        .withBlock(state)
                        .withHand(hand);
                GroovyBlockBreakEvents.fire(ctx); // Fire to the specific event class
            }
            return ActionResult.PASS;
        });
    }

    private static void registerPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EventContext ctx = new EventContext("playerJoin")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            GroovyBlockBreakEvents.fire(ctx); // Fire to the specific event class
        });
    }

    private static void registerPlayerQuit() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            EventContext ctx = new EventContext("playerQuit")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            GroovyBlockBreakEvents.fire(ctx); // Fire to the specific event class
        });
    }

    private static void registerServerTick() {
        ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
            EventContext ctx = new EventContext("serverTick");
            GroovyServerTickEvents.fire(ctx); // Fire to the specific event class
        });
    }

    private static void registerPlayerInteractItem() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // FIX: UseItemCallback requires TypedActionResult<ItemStack>
            if (!world.isClient()) {
                EventContext ctx = new EventContext("playerInteractItem")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withHand(hand);
                GroovyPlayerInteractItemEvents.fire(ctx);
            }
            // Return the current stack, indicating no change to it.
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    private static void registerEntityDeath() {
        // FIX: Using LivingEntityEvents.AFTER_DEATH for cleaner entity death detection
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            World world = livingEntity.getWorld(); // Get world from the entity
            if (!world.isClient()) { // Ensure it's server-side
                EventContext ctx = new EventContext("entityDeath")
                        .withEntity(livingEntity)
                        .withWorld((ServerWorld) world); // Safe cast on server
                GroovyEntityDeathEvents.fire(ctx);
            }
        });
    }


    // --- CLIENT EVENTS ---

    private static void registerClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EventContext ctx = new EventContext("clientTick");
            GroovyClientTickEvents.fire(ctx); // Fire to the specific event class
        });
    }

    // --- REGISTRATION EVENTS ---

    private static void fireRegisterItemEvent() {
        EventContext ctx = new EventContext("registerItem");
        GroovyRegisterItemEvents.fire(ctx);
    }

    private static void fireRegisterBlockEvent() {
        EventContext ctx = new EventContext("registerBlock");
        GroovyRegisterBlockEvents.fire(ctx);
    }
}