package io.github.luckymcdev.groovyengine.scripting.eventservice;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity; // Added for generic entity events
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand; // Useful for interactions

// You might consider adding more fields as needed for specific events,
// or even a Map<String, Object> for very generic data.
public class EventContext {
    public final String event; // Name of the event, mostly for debugging
    public PlayerEntity player;
    public BlockState block;
    public BlockPos pos;
    public ServerWorld world;
    public Entity entity; // For entity-related events
    public Hand hand; // For interaction events

    public EventContext(String event) {
        this.event = event;
    }

    public EventContext withPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }

    public EventContext withBlock(BlockState block) {
        this.block = block;
        return this;
    }

    public EventContext withPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public EventContext withWorld(ServerWorld world) {
        this.world = world;
        return this;
    }

    public EventContext withEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public EventContext withHand(Hand hand) {
        this.hand = hand;
        return this;
    }
}