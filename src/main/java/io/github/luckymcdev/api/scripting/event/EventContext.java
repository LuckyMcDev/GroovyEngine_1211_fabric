package io.github.luckymcdev.api.scripting.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class EventContext {
    public final String event;
    public PlayerEntity player;
    public BlockState block;
    public BlockPos pos;
    public ServerWorld world;

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
}

