import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult
import net.minecraft.item.ItemStack

Logger.info("TestEvents initializing...")

// Tick Events (no return needed)
TickEvents.onStartClientTick { ctx ->
    //Logger.info("TickEvents: Start Client Tick - Event: ${ctx.event}")
}

TickEvents.onEndClientTick { ctx ->
    //Logger.info("TickEvents: End Client Tick - Event: ${ctx.event}")
}

// Player Events
PlayerEvents.onBlockBreak { ctx ->
    Logger.info("PlayerEvents: Block Broken at ${ctx.pos} by ${ctx.player} - Event: ${ctx.event}")
    return ActionResult.PASS
}

// Must return ActionResult
PlayerEvents.onBlockUse { ctx ->
    Logger.info("PlayerEvents: Block Used by ${ctx.player} at ${ctx.blockHitResult.blockPos} - Event: ${ctx.event}")
    return ActionResult.PASS
}

// Must return TypedActionResult<ItemStack>
PlayerEvents.onItemUse { ctx ->
    Logger.info("PlayerEvents: Item Used by ${ctx.player} with hand ${ctx.hand} - Event: ${ctx.event}")
    return new TypedActionResult<ItemStack>(ActionResult.PASS, ctx.player.getStackInHand(ctx.hand))
}

// Must return ActionResult
PlayerEvents.onEntityUse { ctx ->
    Logger.info("PlayerEvents: Entity Used by ${ctx.player} on ${ctx.entity} with hand ${ctx.hand} - Event: ${ctx.event}")
    return ActionResult.PASS
}

// Must return ActionResult
PlayerEvents.onEntityAttack { ctx ->
    Logger.info("PlayerEvents: Entity Attacked by ${ctx.player} on ${ctx.entity} with ${ctx.hand} in ${ctx.world} - Event: ${ctx.event}")
    return ActionResult.PASS
}

// Must return ActionResult
PlayerEvents.onBlockAttack { ctx ->
    Logger.info("PlayerEvents: Block Attacked at ${ctx.pos} by ${ctx.player} - Event: ${ctx.event}")
    return ActionResult.PASS
}

// Connection Events (no return needed)
ConnectionEvents.onClientJoin { ctx ->
    Logger.info("ConnectionEvents: Client joined - Event: ${ctx.event}")
}

ConnectionEvents.onClientDisconnect { ctx ->
    Logger.info("ConnectionEvents: Client disconnected - Event: ${ctx.event}")
}

ConnectionEvents.onServerJoin { ctx ->
    Logger.info("ConnectionEvents: Server joined - Player: ${ctx.serverPlayer} - Event: ${ctx.event}")
}

ConnectionEvents.onServerDisconnect { ctx ->
    Logger.info("ConnectionEvents: Server disconnected - Player: ${ctx.serverPlayer} - Event: ${ctx.event}")
}

// GUI Events (no return needed)
GuiEvents.onScreenInit { ctx ->
    Logger.info("GuiEvents: Screen initialized - ${ctx.screen?.class?.simpleName} - Event: ${ctx.event}")
}

// World Events (no return needed)
WorldEvents.onLoad { ctx ->
    Logger.info("WorldEvents: World Loaded - ${ctx.serverWorld?.registryKey} - Event: ${ctx.event}")
}

WorldEvents.onUnload { ctx ->
    Logger.info("WorldEvents: World Unloaded - ${ctx.serverWorld?.registryKey} - Event: ${ctx.event}")
}

// Command Events (no return needed)
CommandEvents.onRegister { ctx ->
    Logger.info("CommandEvents: Commands registering - Event: ${ctx.event}")
}
