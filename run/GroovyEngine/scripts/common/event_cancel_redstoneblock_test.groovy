//priority=100

import net.minecraft.block.Blocks

PlayerEvents.onBlockBreak { ctx ->
    if (ctx.blockState.block.translationKey.contains("stone")) {
        Logger.info("Replacing stone with redstone at ${ctx.pos}")
        ctx.world.setBlockState(ctx.pos, net.minecraft.block.Blocks.REDSTONE_BLOCK.defaultState)
        return ActionResult.FAIL // cancel the original break
    }
    return ActionResult.PASS // allow break
}