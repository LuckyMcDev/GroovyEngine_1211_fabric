Logger.info("TestEvents initializing...")

// Tick Events
TickEvents.onStartClientTick {
    //Logger.info("TickEvents: Start Client Tick")
}
TickEvents.onEndClientTick {
    //Logger.info("TickEvents: End Client Tick")
}

// Player Events
PlayerEvents.onBlockBreak { player, world, pos, state, entity ->
    Logger.info("PlayerEvents: Block Broken at $pos by $player")
    return false  // returning false is okay here if the callback allows it
}
PlayerEvents.onBlockUse { player, world, hand, hitResult ->
    Logger.info("PlayerEvents: Block Used by $player at ${hitResult.blockPos}")
    return ActionResult.PASS
}
PlayerEvents.onItemUse { player, world, hand ->
    Logger.info("PlayerEvents: Item Used by $player with hand $hand")
    return ActionResult.PASS
}
PlayerEvents.onEntityUse { player, entity, hand ->
    Logger.info("PlayerEvents: Entity Used by $player on $entity with hand $hand")
    return ActionResult.PASS
}
PlayerEvents.onEntityAttack { player, entity ->
    Logger.info("PlayerEvents: Entity Attacked by $player on $entity")
    return ActionResult.PASS
}
PlayerEvents.onBlockAttack { player, world, pos, state, entity ->
    Logger.info("PlayerEvents: Block Attacked at $pos by $player")
    return ActionResult.PASS
}

// Connection Events
ConnectionEvents.onClientJoin { handler, sender, client ->
    Logger.info("ConnectionEvents: Client joined")
}
ConnectionEvents.onClientDisconnect { handler, client ->
    Logger.info("ConnectionEvents: Client disconnected")
}
ConnectionEvents.onServerJoin { handler, sender, server ->
    Logger.info("ConnectionEvents: Server joined")
}
ConnectionEvents.onServerDisconnect { handler, server ->
    Logger.info("ConnectionEvents: Server disconnected")
}

// GUI Events
GuiEvents.onScreenInit { client, screen, scaledWidth, scaledHeight ->
    Logger.info("GuiEvents: Screen initialized - ${screen?.class?.simpleName}")
}
GuiEvents.onScreenRender { client, screen, matrices, mouseX, mouseY, tickDelta ->
    // too spammy for logging every frame
}
GuiEvents.onTooltip { stack, lines, context ->
    lines.add("Tooltip added by GroovyEngine TestEvents")
}

// Loot Events
LootEvents.onModify { resourceManager, lootManager, id, table, setter ->
    Logger.info("LootEvents: Loot Table Modified - $id")
}

// World Events
WorldEvents.onLoad { server, world ->
    Logger.info("WorldEvents: World Loaded - ${world?.registryKey}")
}
WorldEvents.onUnload { server, world ->
    Logger.info("WorldEvents: World Unloaded - ${world?.registryKey}")
}

// Command Events
CommandEvents.onRegister { dispatcher, registryAccess, environment ->
    Logger.info("CommandEvents: Commands registering")
}
