//priority=1
package scripts.common

BlockBuilder.register("banana_block")
        .displayName("Banana Block")
        .texture("groovyengine:block/banana_block")
        .build()

ItemBuilder.register("banana")
        .displayName("Banana")
        .build()

// --- Recipes ---

RecipeBuilder.shaped("banana_block")
        .pattern("BBB", "BBB", "BBB")
        .key("B", "groovyengine:banana")
        .output("groovyengine:banana_block")
        .buildAndGenerate()

RecipeBuilder.shapeless("banana_block_unpack")
        .ingredients("groovyengine:banana_block")
        .output("groovyengine:banana", 9)
        .buildAndGenerate()

RecipeBuilder.smelting("smelt_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:dried_kelp")
        .xp(0.35)
        .time(200)
        .buildAndGenerate()

RecipeBuilder.blasting("blast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:charcoal")
        .xp(0.15)
        .time(100)
        .buildAndGenerate()

RecipeBuilder.smoking("smoke_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:cooked_rabbit")
        .xp(0.3)
        .time(100)
        .buildAndGenerate()

RecipeBuilder.campfire("campfire_roast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:baked_potato")
        .xp(0.2)
        .time(600)
        .buildAndGenerate()

Logger.error("""
    Now! All the banana items and recipes are registered. This includes:
    - The 'banana' item
    - The 'banana_block' block
    - A shaped recipe to craft the block from bananas
    - A shapeless recipe to unpack the block into 9 bananas
    - A smelting recipe (banana → dried_kelp)
    - A blasting recipe (banana → charcoal)
    - A smoking recipe (banana → cooked_rabbit)
    - A campfire cooking recipe (banana → baked_potato)
    """)