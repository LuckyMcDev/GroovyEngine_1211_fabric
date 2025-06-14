package io.github.luckymcdev.groovyengine.script_event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;

public class EventRegistry {

    public static void initServer() {
        registerBlockBreak();
        registerBlockPlace();
        registerPlayerJoin();
        registerPlayerQuit();
        registerServerTick();
        registerItem();
        registerBlock();
    }

    public static void initClient() {
        registerClientTick();
        registerItem();
        registerBlock();
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
                        .withBlock(state);
                Events.trigger("blockBreak", ctx);
            }
            return ActionResult.PASS;
        });
    }

    private static void registerBlockPlace() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                BlockPos pos = hitResult.getBlockPos();
                BlockState state = world.getBlockState(pos);
                EventContext ctx = new EventContext("blockPlace")
                        .withPlayer(player)
                        .withWorld((ServerWorld) world)
                        .withPos(pos)
                        .withBlock(state);
                Events.trigger("blockPlace", ctx);
            }
            return ActionResult.PASS;
        });
    }

    private static void registerPlayerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EventContext ctx = new EventContext("playerJoin")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            Events.trigger("playerJoin", ctx);
        });
    }

    private static void registerPlayerQuit() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            EventContext ctx = new EventContext("playerQuit")
                    .withPlayer(handler.player)
                    .withWorld(handler.player.getServerWorld());
            Events.trigger("playerQuit", ctx);
        });
    }

    private static void registerServerTick() {
        ServerTickEvents.START_SERVER_TICK.register((MinecraftServer server) -> {
            EventContext ctx = new EventContext("serverTick");
            Events.trigger("serverTick", ctx);
        });
    }

    // --- CLIENT EVENTS ---

    private static void registerClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            EventContext ctx = new EventContext("clientTick");
            Events.trigger("clientTick", ctx);
        });
    }

    // --- SHARED ---

    private static void registerItem() {
        EventContext ctx = new EventContext("registerItem");
        Events.trigger("registerItem", ctx);
    }

    private static void registerBlock() {
        EventContext ctx = new EventContext("registerBlock");
        Events.trigger("registerBlock", ctx);
    }
}
