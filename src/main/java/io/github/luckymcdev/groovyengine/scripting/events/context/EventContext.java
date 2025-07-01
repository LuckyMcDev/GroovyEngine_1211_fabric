package io.github.luckymcdev.groovyengine.scripting.events.context;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class EventContext {
    public final String event;

    // Player/Entity related
    public PlayerEntity player;
    public ServerPlayerEntity serverPlayer;
    public Entity entity;
    public BlockEntity blockEntity;

    // Block/World related
    public BlockState blockState;
    public BlockPos pos;
    public World world;
    public ServerWorld serverWorld;
    public Direction direction;
    public Hand hand;
    public BlockHitResult blockHitResult;
    public EntityHitResult entityHitResult;

    // Client/Server related
    public MinecraftServer server;
    public MinecraftClient client;
    public Screen screen;
    public int scaledWidth;
    public int scaledHeight;

    // Item/Tooltip related
    public ItemStack itemStack;
    public TooltipContext tooltipContext;
    public TooltipType tooltipType;
    public List<Text> tooltipLines;

    // Command related
    public CommandDispatcher<ServerCommandSource> commandDispatcher;
    public CommandRegistryAccess commandRegistryAccess;
    public RegistrationEnvironment commandEnvironment;

    // Connection related
    public PacketSender packetSender;
    public ClientPlayNetworkHandler clientPlayNetworkHandler;

    public EventContext(String event) {
        this.event = event;
    }

    // Player/Entity methods
    public EventContext withPlayer(PlayerEntity player) {
        this.player = player;
        return this;
    }

    public EventContext withServerPlayer(ServerPlayerEntity serverPlayer) {
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

    public EventContext withWorld(World world) {
        this.world = world;
        return this;
    }

    public EventContext withServerWorld(ServerWorld serverWorld) {
        this.serverWorld = serverWorld;
        this.world = serverWorld; // Also set the general world field
        return this;
    }

    public EventContext withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public EventContext withHand(Hand hand) {
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

    public EventContext withClient(MinecraftClient client) {
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

    public EventContext withTooltipType(TooltipType tooltipType) {
        this.tooltipType = tooltipType;
        return this;
    }

    public EventContext withTooltipLines(List<Text> tooltipLines) {
        this.tooltipLines = tooltipLines;
        return this;
    }

    // Command methods
    public EventContext withCommandDispatcher(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
        return this;
    }

    public EventContext withCommandRegistryAccess(CommandRegistryAccess commandRegistryAccess) {
        this.commandRegistryAccess = commandRegistryAccess;
        return this;
    }

    public EventContext withCommandEnvironment(RegistrationEnvironment commandEnvironment) {
        this.commandEnvironment = commandEnvironment;
        return this;
    }

    // Connection methods
    public EventContext withPacketSender(PacketSender packetSender) {
        this.packetSender = packetSender;
        return this;
    }

    public EventContext withClientPlayNetworkHandler(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.clientPlayNetworkHandler = clientPlayNetworkHandler;
        return this;
    }

    // Legacy compatibility methods (keeping your original methods)
    public EventContext withBlock(BlockState block) {
        return withBlockState(block);
    }
}