//priority=0
package scripts.common

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

Logger.info("hello v 2")


PlayerEvents.onItemUse { ctx ->
    Logger.info("PlayerEvents: Item Used by ${ctx.player} with hand ${ctx.hand} - Event: ${ctx.event}")
    return new TypedActionResult<ItemStack>(ActionResult.PASS, ctx.player.getStackInHand(ctx.hand))
}

def helloWorld() {
    return "Hello from closure!"
}

Globals.put("helloWorld", " hello !!!!!!!") // store reference