import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult
import net.minecraft.item.ItemStack

Logger.info("TestEvents initializing...")

// Tick Events
TickEvents.onStartClientTick {
    //Logger.info("TickEvents: Start Client Tick")
}
TickEvents.onEndClientTick {
    //Logger.info("TickEvents: End Client Tick")
}

// Player Events (CORRECTED)
PlayerEvents.onBlockBreak { player, world, pos, state, entity ->  // No parentheses
    Logger.info("PlayerEvents: Block Broken at $pos by $player")
}

PlayerEvents.onBlockUse { player, world, hand, hitResult ->
    Logger.info("PlayerEvents: Block Used by $player at ${hitResult.blockPos}")
    return ActionResult.PASS
}

PlayerEvents.onItemUse { player, world, hand ->
    Logger.info("PlayerEvents: Item Used by $player with hand $hand")
    return new TypedActionResult<ItemStack>(ActionResult.PASS, player.getStackInHand(hand))
}

PlayerEvents.onEntityUse { player, world, hand, entity, hitResult ->
    Logger.info("PlayerEvents: Entity Used by $player on $entity with hand $hand")
    return ActionResult.PASS
}

// CRITICAL FIX: Match Fabric API signature
PlayerEvents.onEntityAttack { player, world, hand, entity, hitResult ->
    Logger.info("PlayerEvents: Entity Attacked by $player on $entity with $hand in $world")
    return ActionResult.PASS
}

PlayerEvents.onBlockAttack { player, world, hand, pos, direction ->
    Logger.info("PlayerEvents: Block Attacked at $pos by $player")
    return ActionResult.PASS
}

// Connection Events
ConnectionEvents.onClientJoin { handler, sender, client ->
    Logger.info("ConnectionEvents: Client joined")
    return // no return needed
}
ConnectionEvents.onClientDisconnect { handler, client ->
    Logger.info("ConnectionEvents: Client disconnected")
    return
}
ConnectionEvents.onServerJoin { handler, sender, server ->
    Logger.info("ConnectionEvents: Server joined")
    return
}
ConnectionEvents.onServerDisconnect { handler, server ->
    Logger.info("ConnectionEvents: Server disconnected")
    return
}

// GUI Events
GuiEvents.onScreenInit { client, screen, scaledWidth, scaledHeight ->
    Logger.info("GuiEvents: Screen initialized - ${screen?.class?.simpleName}")
    return
}
GuiEvents.onScreenRender { client, screen, matrices, mouseX, mouseY, tickDelta ->
    // too spammy for logging every frame
    return
}
GuiEvents.onTooltip { stack, lines, context ->
    lines.add("Tooltip added by GroovyEngine TestEvents")
    return
}

// Loot Events
LootEvents.onModify { resourceManager, lootManager, id, table, setter ->
    Logger.info("LootEvents: Loot Table Modified - $id")
    return
}

// World Events
WorldEvents.onLoad { server, world ->
    Logger.info("WorldEvents: World Loaded - ${world?.registryKey}")
    return
}
WorldEvents.onUnload { server, world ->
    Logger.info("WorldEvents: World Unloaded - ${world?.registryKey}")
    return
}

// Command Events
CommandEvents.onRegister { dispatcher, registryAccess, environment ->
    Logger.info("CommandEvents: Commands registering")
    return
}
