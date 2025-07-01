//priority=1
package scripts.common

// Standardized BlockBuilder usage
BlockBuilder.register("banana_block")
        .displayName("Banana Block")
        .texture("groovyengine:block/banana_block")
        .build()

// Standardized ItemBuilder usage
ItemBuilder.register("banana")
        .displayName("Banana")
        .build()

// --- Standardized Recipe Builders ---

// Shaped recipe
RecipeBuilder.shaped("banana_block")
        .pattern("BBB", "BBB", "BBB")
        .key("B", "groovyengine:banana")
        .output("groovyengine:banana_block")
        .build() // Changed from buildAndGenerate()

// Shapeless recipe
RecipeBuilder.shapeless("banana_block_unpack")
        .ingredients("groovyengine:banana_block")
        .output("groovyengine:banana", 9)
        .build()

// Cooking recipes
RecipeBuilder.smelting("smelt_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:dried_kelp")
        .xp(0.35)
        .time(200)
        .build()

RecipeBuilder.blasting("blast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:charcoal")
        .xp(0.15)
        .time(100)
        .build()

RecipeBuilder.smoking("smoke_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:cooked_rabbit")
        .xp(0.3)
        .time(100)
        .build()

RecipeBuilder.campfire("campfire_roast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:baked_potato")
        .xp(0.2)
        .time(600)
        .build()

Logger.info("""
    All banana items and recipes registered:
    - Banana item
    - Banana block
    - 6 different recipes
    """)