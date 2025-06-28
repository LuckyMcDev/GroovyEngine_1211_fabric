package io.github.luckymcdev.groovyengine.scripting.events;

import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.event.player.*;
import groovy.lang.Closure;

public class PlayerEvents {

    public static void onBlockBreak(Closure<Void> closure) {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            EventContext ctx = new EventContext("block_break")
                    .withPlayer(player)
                    .withWorld(world)
                    .withPos(pos)
                    .withBlockState(state)
                    .withBlockEntity(entity);
            closure.call(ctx);
        });
    }

    public static void onBlockUse(Closure<?> closure) {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            EventContext ctx = new EventContext("block_use")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand)
                    .withBlockHitResult(hitResult);
            return (net.minecraft.util.ActionResult) closure.call(ctx);
        });
    }

    public static void onItemUse(Closure<?> closure) {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            EventContext ctx = new EventContext("item_use")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand);
            return (net.minecraft.util.TypedActionResult) closure.call(ctx);
        });
    }

    public static void onEntityUse(Closure<?> closure) {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            EventContext ctx = new EventContext("entity_use")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand)
                    .withEntity(entity)
                    .withEntityHitResult(hitResult);
            return (net.minecraft.util.ActionResult) closure.call(ctx);
        });
    }

    public static void onEntityAttack(Closure<?> closure) {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            EventContext ctx = new EventContext("entity_attack")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand)
                    .withEntity(entity)
                    .withEntityHitResult(hitResult);
            return (net.minecraft.util.ActionResult) closure.call(ctx);
        });
    }

    public static void onBlockAttack(Closure<?> closure) {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            EventContext ctx = new EventContext("block_attack")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand)
                    .withPos(pos)
                    .withDirection(direction);
            return (net.minecraft.util.ActionResult) closure.call(ctx);
        });
    }
}