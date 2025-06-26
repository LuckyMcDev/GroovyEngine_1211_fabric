package io.github.luckymcdev.groovyengine.scripting.events;

import net.fabricmc.fabric.api.event.player.*;

public class PlayerEvents {
    public static void onBlockBreak(PlayerBlockBreakEvents.After callback) {
        PlayerBlockBreakEvents.AFTER.register(callback);
    }

    public static void onBlockUse(UseBlockCallback callback) {
        UseBlockCallback.EVENT.register(callback);
    }

    public static void onItemUse(UseItemCallback callback) {
        UseItemCallback.EVENT.register(callback);
    }

    public static void onEntityUse(UseEntityCallback callback) {
        UseEntityCallback.EVENT.register(callback);
    }

    public static void onEntityAttack(AttackEntityCallback callback) {
        AttackEntityCallback.EVENT.register(callback);
    }

    public static void onBlockAttack(AttackBlockCallback callback) {
        AttackBlockCallback.EVENT.register(callback);
    }
}
