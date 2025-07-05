package io.github.luckymcdev.groovyengine.scripting.events.context;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import java.util.List;

public class EventContext {
    public final String event;

    // Player/Entity related
    public Player player;
    public ServerPlayer serverPlayer;
    public Entity entity;
    public BlockEntity blockEntity;

    // Block/World related
    public BlockState blockState;
    public BlockPos pos;
    public Level world;
    public ServerLevel serverWorld;
    public Direction direction;
    public InteractionHand hand;
    public BlockHitResult blockHitResult;
    public EntityHitResult entityHitResult;

    // Client/Server related
    public MinecraftServer server;
    public Minecraft client;
    public Screen screen;
    public int scaledWidth;
    public int scaledHeight;

    // Item/Tooltip related
    public ItemStack itemStack;
    public TooltipContext tooltipContext;
    public TooltipFlag tooltipType;
    public List<Component> tooltipLines;

    // Command related
    public CommandDispatcher<CommandSourceStack> commandDispatcher;
    public CommandBuildContext commandRegistryAccess;
    public CommandSelection commandEnvironment;

    // Connection related
    public PacketSender packetSender;
    public ClientPacketListener clientPlayNetworkHandler;

    public EventContext(String event) {
        this.event = event;
    }

    // Player/Entity methods
    public EventContext withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public EventContext withServerPlayer(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
        this.player = serverPlayer; // Also set the general player field
        return this;
    }

    public EventContext withEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public EventContext withBlockEntity(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        return this;
    }

    // Block/World methods
    public EventContext withBlockState(BlockState blockState) {
        this.blockState = blockState;
        return this;
    }

    public EventContext withPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public EventContext withWorld(Level world) {
        this.world = world;
        return this;
    }

    public EventContext withServerWorld(ServerLevel serverWorld) {
        this.serverWorld = serverWorld;
        this.world = serverWorld; // Also set the general world field
        return this;
    }

    public EventContext withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public EventContext withHand(InteractionHand hand) {
        this.hand = hand;
        return this;
    }

    public EventContext withBlockHitResult(BlockHitResult blockHitResult) {
        this.blockHitResult = blockHitResult;
        return this;
    }

    public EventContext withEntityHitResult(EntityHitResult entityHitResult) {
        this.entityHitResult = entityHitResult;
        return this;
    }

    // Client/Server methods
    public EventContext withServer(MinecraftServer server) {
        this.server = server;
        return this;
    }

    public EventContext withClient(Minecraft client) {
        this.client = client;
        return this;
    }

    public EventContext withScreen(Screen screen) {
        this.screen = screen;
        return this;
    }

    public EventContext withScreenSize(int scaledWidth, int scaledHeight) {
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        return this;
    }

    // Item/Tooltip methods
    public EventContext withItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public EventContext withTooltipContext(TooltipContext tooltipContext) {
        this.tooltipContext = tooltipContext;
        return this;
    }

    public EventContext withTooltipType(TooltipFlag tooltipType) {
        this.tooltipType = tooltipType;
        return this;
    }

    public EventContext withTooltipLines(List<Component> tooltipLines) {
        this.tooltipLines = tooltipLines;
        return this;
    }

    // Command methods
    public EventContext withCommandDispatcher(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
        return this;
    }

    public EventContext withCommandRegistryAccess(CommandBuildContext commandRegistryAccess) {
        this.commandRegistryAccess = commandRegistryAccess;
        return this;
    }

    public EventContext withCommandEnvironment(CommandSelection commandEnvironment) {
        this.commandEnvironment = commandEnvironment;
        return this;
    }

    // Connection methods
    public EventContext withPacketSender(PacketSender packetSender) {
        this.packetSender = packetSender;
        return this;
    }

    public EventContext withClientPlayNetworkHandler(ClientPacketListener clientPlayNetworkHandler) {
        this.clientPlayNetworkHandler = clientPlayNetworkHandler;
        return this;
    }

    // Legacy compatibility methods (keeping your original methods)
    public EventContext withBlock(BlockState block) {
        return withBlockState(block);
    }
}