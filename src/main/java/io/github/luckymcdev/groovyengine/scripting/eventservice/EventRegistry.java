package io.github.luckymcdev.groovyengine.scripting.eventservice;

import io.github.luckymcdev.groovyengine.scripting.eventservice.events.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class EventRegistry {

    public static void initServer() {
        clearAllEvents();

        registerBlockBreak();
        registerBlockPlace();
        registerPlayerJoin();
        registerPlayerQuit();
        registerServerTick();
        registerPlayerInteractItem();
        registerEntityDeath();
    }

    public static void initClient() {
        clearAllEvents();

        registerClientTick();
    }

    public static void clearAllEvents() {
        GroovyClientTickEvents.clear();
        GroovyServerTickEvents.clear();
        GroovyBlockBreakEvents.clear();
        GroovyBlockPlaceEvents.clear();
        GroovyPlayerJoinEvents.clear();
        GroovyPlayerLeaveEvents.clear();
        GroovyPlayerInteractItemEvents.clear();
        GroovyEntityDeathEvents.clear();
        GroovyRegisterItemEvents.clear();
        GroovyRegisterBlockEvents.clear();
        GroovyShaderEvents.clear();
    }

    // --- SERVER EVENTS ---

    private static void registerBlockBreak() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!world.isClient()) {
                BlockState state = world.getBlockState(pos);
                EventContext ctx = new EventContext("blockBreak")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withPos(pos)
                        .withBlock(state)
                        .withHand(hand);
                GroovyBlockBreakEvents.fire(ctx);
            }
            return ActionResult.PASS;
        });
    }

    private static void registerBlockPlace() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient()) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                EventContext ctx = new EventContext("blockPlace")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withPos(pos)
                        .withBlock(state)
                        .withHand(hand);
                GroovyBlockPlaceEvents.fire(ctx); // Fixed to block place events
            }
            return ActionResult.PASS;
        });
    }

    private static void registerPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EventContext ctx = new EventContext("playerJoin")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            GroovyPlayerJoinEvents.fire(ctx);
        });
    }

    private static void registerPlayerQuit() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            EventContext ctx = new EventContext("playerQuit")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            GroovyPlayerLeaveEvents.fire(ctx);
        });
    }

    private static void registerServerTick() {
        ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
            EventContext ctx = new EventContext("serverTick");
            GroovyServerTickEvents.fire(ctx);
        });
    }

    private static void registerPlayerInteractItem() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) {
                EventContext ctx = new EventContext("playerInteractItem")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withHand(hand);
                GroovyPlayerInteractItemEvents.fire(ctx);
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }

    private static void registerEntityDeath() {
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            World world = livingEntity.getWorld();
            if (!world.isClient()) {
                EventContext ctx = new EventContext("entityDeath")
                        .withEntity(livingEntity)
                        .withWorld((ServerWorld) world);
                GroovyEntityDeathEvents.fire(ctx);
            }
        });
    }

    // --- CLIENT EVENTS ---

    private static void registerClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EventContext ctx = new EventContext("clientTick");
            GroovyClientTickEvents.fire(ctx);
        });
    }
}
