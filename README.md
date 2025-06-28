# GroovyEngine

**GroovyEngine** is a lightweight scripting layer for [Minecraft Fabric](https://fabricmc.net/), designed to drastically simplify mod development using the [Groovy](https://groovy-lang.org/) programming language.

Rather than writing and compiling full Java mods, you can write Groovy scripts and drop them into your `.minecraft/GroovyEngine/` folder — and see your changes reflected live in-game.

## Features



## Development Status

**GroovyEngine is in early development and not yet considered stable.**  
Some APIs may change without warning, and game-breaking bugs or performance issues are expected.

We **do not recommend using it in production modpacks** just yet — but it's great for prototyping and experimentation.

## Getting Started

Drop Groovy scripts in:

`.minecraft/GroovyEngine/ `


Scripts are auto-discovered and executed based on their content and location (e.g., client/server).


### Quick Example:

```groovy

PlayerEvents.onItemUse { ctx ->
    Logger.info("PlayerEvents: Item Used by ${ctx.player} with hand ${ctx.hand} - Event: ${ctx.event}")
    return new TypedActionResult<ItemStack>(ActionResult.PASS, ctx.player.getStackInHand(ctx.hand))
}
```
> For more examples, see the `run/GroovyEngine` folder on the GitHub.