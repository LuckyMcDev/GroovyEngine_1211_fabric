
Logger.info("TestEvents (client) initializing...")

TickEvents.onStartClientTick { ctx ->
    //Logger.info("TickEvents: End Client Tick - Event: ${ctx.event}")
}

TickEvents.onEndClientTick { ctx ->
    //Logger.info("TickEvents: End Client Tick - Event: ${ctx.event}")
}

// Connection Events
ConnectionEvents.onClientJoin { ctx ->
    Logger.info("ConnectionEvents: Client joined - Event: ${ctx.event}")
}

ConnectionEvents.onClientDisconnect { ctx ->
    Logger.info("ConnectionEvents: Client disconnected - Event: ${ctx.event}")
}

// Connection Events
ConnectionEvents.onServerJoin { ctx ->
    Logger.info("ConnectionEvents: Server joined - Player: ${ctx.serverPlayer} - Event: ${ctx.event}")
}

ConnectionEvents.onServerDisconnect { ctx ->
    Logger.info("ConnectionEvents: Server disconnected - Player: ${ctx.serverPlayer} - Event: ${ctx.event}")
}

// GUI Events
GuiEvents.onScreenInit { ctx ->
    Logger.info("GuiEvents: Screen initialized - ${ctx.screen?.class?.simpleName} - Event: ${ctx.event}")
}
