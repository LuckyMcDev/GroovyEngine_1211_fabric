//disabled

import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult
import net.minecraft.item.ItemStack

Logger.info("TestEvents (common) initializing...")

// Player Events
PlayerEvents.onBlockBreak { ctx ->
    //Logger.info("PlayerEvents: Block Broken at ${ctx.pos} by ${ctx.player} - Event: ${ctx.event}")
    return ActionResult.PASS
}

PlayerEvents.onBlockUse { ctx ->
    Logger.info("PlayerEvents: Block Used by ${ctx.player} at ${ctx.blockHitResult.blockPos} - Event: ${ctx.event}")
    return ActionResult.PASS
}

PlayerEvents.onItemUse { ctx ->
    Logger.info("PlayerEvents: Item Used by ${ctx.player} with hand ${ctx.hand} - Event: ${ctx.event}")
    return new TypedActionResult<ItemStack>(ActionResult.PASS, ctx.player.getStackInHand(ctx.hand))
}

PlayerEvents.onEntityUse { ctx ->
    Logger.info("PlayerEvents: Entity Used by ${ctx.player} on ${ctx.entity} with hand ${ctx.hand} - Event: ${ctx.event}")
    return ActionResult.PASS
}

PlayerEvents.onEntityAttack { ctx ->
    Logger.info("PlayerEvents: Entity Attacked by ${ctx.player} on ${ctx.entity} with ${ctx.hand} in ${ctx.world} - Event: ${ctx.event}")
    return ActionResult.PASS
}

PlayerEvents.onBlockAttack { ctx ->
    Logger.info("PlayerEvents: Block Attacked at ${ctx.pos} by ${ctx.player} - Event: ${ctx.event}")
    return ActionResult.PASS
}