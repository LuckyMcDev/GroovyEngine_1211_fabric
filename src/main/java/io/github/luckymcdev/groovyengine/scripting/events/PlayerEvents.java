package io.github.luckymcdev.groovyengine.scripting.events;

import io.github.luckymcdev.groovyengine.scripting.events.context.EventContext;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.item.ItemStack;
import groovy.lang.Closure;

public class PlayerEvents {

    public static void onBlockBreak(Closure<?> closure) {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            EventContext ctx = new EventContext("block_break")
                    .withPlayer(player)
                    .withWorld(world)
                    .withPos(pos)
                    .withBlockState(state)
                    .withBlockEntity(blockEntity);

            Object result = closure.call(ctx);

            if (result instanceof ActionResult) {
                ActionResult action = (ActionResult) result;
                return action != ActionResult.FAIL; // false = cancel
            }

            // fallback to default
            return true;
        });
    }


    public static void onBlockUse(Closure<?> closure) {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            EventContext ctx = new EventContext("block_use")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand)
                    .withBlockHitResult(hitResult);
            Object result = closure.call(ctx);
            return result instanceof ActionResult ? (ActionResult) result : ActionResult.PASS;
        });
    }

    public static void onItemUse(Closure<?> closure) {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            EventContext ctx = new EventContext("item_use")
                    .withPlayer(player)
                    .withWorld(world)
                    .withHand(hand);
            Object result = closure.call(ctx);
            return result instanceof TypedActionResult ? (TypedActionResult<ItemStack>) result :
                    new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
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
            Object result = closure.call(ctx);
            return result instanceof ActionResult ? (ActionResult) result : ActionResult.PASS;
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
            Object result = closure.call(ctx);
            return result instanceof ActionResult ? (ActionResult) result : ActionResult.PASS;
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
            Object result = closure.call(ctx);
            return result instanceof ActionResult ? (ActionResult) result : ActionResult.PASS;
        });
    }
}
