//priority=1
package scripts.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
// Removed Material import - not needed in 1.21.1
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;

public class CustomTestItem extends Item {
    public CustomTestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            user.sendMessage(Text.literal("Right-clicked with CustomTestItem!"), false);
        }
        return TypedActionResult.success(stack);
    }
}

public class CustomTestBlock extends Block {
    public CustomTestBlock() {
        // Updated for 1.21.1 - using create() instead of of() and no Material
        super(AbstractBlock.Settings
                .create()
                .strength(4.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL));
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("You clicked the CustomTestBlock!"), false);
        }
        return ItemActionResult.SUCCESS;
    }
}

BlockBuilder.registerCustom("custom_test_block", new CustomTestBlock())
        .displayName("Custom Test Block")
        .texture("modid:block/custom_test_block")
        .build();

ItemBuilder.registerCustom("test_item", new CustomTestItem(new Item.Settings()))
        .displayName("Test Item")
        .build();

// Standardized BlockBuilder usage
BlockBuilder.register("banana_block")
        .displayName("Banana Block")
        .texture("groovyengine:block/banana_block")
        .build();

// Standardized ItemBuilder usage
ItemBuilder.register("banana")
        .displayName("Banana")
        .build();

// --- Standardized Recipe Builders ---
// Shaped recipe
RecipeBuilder.shaped("banana_block")
        .pattern("BBB", "BBB", "BBB")
        .key("B", "groovyengine:banana")
        .output("groovyengine:banana_block")
        .build(); // Changed from buildAndGenerate()

// Shapeless recipe
RecipeBuilder.shapeless("banana_block_unpack")
        .ingredients("groovyengine:banana_block")
        .output("groovyengine:banana", 9)
        .build();

// Cooking recipes
RecipeBuilder.smelting("smelt_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:dried_kelp")
        .xp(0.35)
        .time(200)
        .build();

RecipeBuilder.blasting("blast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:charcoal")
        .xp(0.15)
        .time(100)
        .build();

RecipeBuilder.smoking("smoke_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:cooked_rabbit")
        .xp(0.3)
        .time(100)
        .build();

RecipeBuilder.campfire("campfire_roast_banana")
        .ingredient("groovyengine:banana")
        .output("minecraft:baked_potato")
        .xp(0.2)
        .time(600)
        .build();

Logger.info("""
All banana items and recipes registered:
- Banana item
- Banana block  
- 6 different recipes
""");