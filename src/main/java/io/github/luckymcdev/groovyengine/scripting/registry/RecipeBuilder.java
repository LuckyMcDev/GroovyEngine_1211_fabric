package io.github.luckymcdev.groovyengine.scripting.registry;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.generators.datagen.DatapackDataGenerator; // Correctly imports the generator that writes data
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RecipeBuilder {

    // Factory methods to start building different recipe types
    public static ShapedRecipeBuilder shaped(String id) {
        return new ShapedRecipeBuilder(id);
    }

    public static ShapelessRecipeBuilder shapeless(String id) {
        return new ShapelessRecipeBuilder(id);
    }

    public static CookingRecipeBuilder smelting(String id) {
        return new CookingRecipeBuilder(id, "minecraft:smelting");
    }

    public static CookingRecipeBuilder blasting(String id) {
        return new CookingRecipeBuilder(id, "minecraft:blasting");
    }

    public static CookingRecipeBuilder smoking(String id) {
        return new CookingRecipeBuilder(id, "minecraft:smoking");
    }

    public static CookingRecipeBuilder campfire(String id) {
        return new CookingRecipeBuilder(id, "minecraft:campfire_cooking");
    }

    // --- Builder classes for fluent API ---

    public abstract static class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {
        protected Identifier recipeId;
        protected Identifier resultId;
        protected int count = 1;

        public AbstractRecipeBuilder(String id) {
            // All generated recipes will be under the GroovyEngine mod ID namespace
            this.recipeId = Identifier.of(GroovyEngine.MODID, id);
        }

        /**
         * Sets the output item and count for the recipe.
         * @param itemIdentifier The Minecraft item ID (e.g., "minecraft:diamond_sword", "mymod:custom_item").
         * @param count The number of output items.
         * @return The builder instance for chaining.
         */
        public T output(String itemIdentifier, int count) {
            this.resultId = Identifier.of(itemIdentifier);
            this.count = count;
            return (T) this;
        }

        /**
         * Sets the output item for the recipe with a default count of 1.
         * @param itemIdentifier The Minecraft item ID.
         * @return The builder instance for chaining.
         */
        public T output(String itemIdentifier) {
            return output(itemIdentifier, 1);
        }

        /**
         * Finalizes the recipe definition and triggers its generation into the datapack.
         */
        public abstract void buildAndGenerate();
    }

    public static class ShapedRecipeBuilder extends AbstractRecipeBuilder<ShapedRecipeBuilder> {
        private String[] pattern;
        private Map<Character, String> key = new HashMap<>();

        public ShapedRecipeBuilder(String id) {
            super(id);
        }

        /**
         * Defines the pattern for the shaped recipe grid.
         * @param pattern An array of strings, where each string represents a row in the crafting grid.
         * For example, `pattern("AAA", "BBB", "CCC")`.
         * @return The builder instance for chaining.
         */
        public ShapedRecipeBuilder pattern(String... pattern) {
            this.pattern = pattern;
            return this;
        }

        /**
         * Maps a single character symbol from the pattern to an item or tag identifier.
         * @param symbolString A single character string (e.g., "I", "A") used in the pattern.
         * @param itemOrTagIdentifier The Minecraft item ID (e.g., "minecraft:iron_ingot")
         * or a tag ID (e.g., "#minecraft:logs").
         * @return The builder instance for chaining.
         */
        public ShapedRecipeBuilder key(String symbolString, String itemOrTagIdentifier) {
            // Warn if the symbol is not a single character, but still try to use the first char.
            if (symbolString == null || symbolString.length() != 1) {
                GroovyEngine.LOGGER.warn("RecipeBuilder.shaped('{}').key() received a symbol that is not a single character: '{}'. " +
                        "This may lead to unexpected behavior. Only the first character will be used if valid.", recipeId, symbolString);

                if (symbolString == null || symbolString.isEmpty()) {
                    return this; // Cannot use an empty or null string as a key
                }
            }
            this.key.put(symbolString.charAt(0), itemOrTagIdentifier);
            return this;
        }

        @Override
        public void buildAndGenerate() {
            if (pattern == null || pattern.length == 0 || key.isEmpty() || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid shaped recipe for {}: pattern, key, or result missing. Recipe not generated.", recipeId);
                return;
            }
            // Delegate actual JSON generation and file writing to DatapackDataGenerator
            DatapackDataGenerator.generateShapedRecipe(recipeId, pattern, key, resultId, count);
        }
    }

    public static class ShapelessRecipeBuilder extends AbstractRecipeBuilder<ShapelessRecipeBuilder> {
        private String[] ingredients;

        public ShapelessRecipeBuilder(String id) {
            super(id);
        }

        /**
         * Defines the ingredients for the shapeless recipe.
         * @param ingredients An array of item or tag identifiers (e.g., "minecraft:stick", "#minecraft:planks").
         * @return The builder instance for chaining.
         */
        public ShapelessRecipeBuilder ingredients(String... ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        @Override
        public void buildAndGenerate() {
            if (ingredients == null || ingredients.length == 0 || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid shapeless recipe for {}: ingredients or result missing. Recipe not generated.", recipeId);
                return;
            }
            // Delegate actual JSON generation and file writing to DatapackDataGenerator
            DatapackDataGenerator.generateShapelessRecipe(recipeId, ingredients, resultId, count);
        }
    }

    public static class CookingRecipeBuilder extends AbstractRecipeBuilder<CookingRecipeBuilder> {
        private String ingredient;
        private float experience = 0.0f;
        private int cookingTime = 200; // Default to 10 seconds (20 ticks per second)

        private String recipeType;

        public CookingRecipeBuilder(String id, String type) {
            super(id);
            this.recipeType = type;
        }

        /**
         * Sets the single input ingredient for cooking recipes.
         * @param itemOrTagIdentifier The Minecraft item ID or a tag ID for the input.
         * @return The builder instance for chaining.
         */
        public CookingRecipeBuilder ingredient(String itemOrTagIdentifier) {
            this.ingredient = itemOrTagIdentifier;
            return this;
        }

        /**
         * Sets the experience points gained from cooking this recipe.
         * @param value The amount of experience (e.g., 0.7 for smelting ores).
         * @return The builder instance for chaining.
         */
        public CookingRecipeBuilder xp(float value) {
            this.experience = value;
            return this;
        }

        /**
         * Sets the cooking time in ticks. (20 ticks = 1 second).
         * @param ticks The duration of cooking.
         * @return The builder instance for chaining.
         */
        public CookingRecipeBuilder time(int ticks) {
            this.cookingTime = ticks;
            return this;
        }

        @Override
        public void buildAndGenerate() {
            if (ingredient == null || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid {} recipe for {}: ingredient or result missing. Recipe not generated.", recipeType, recipeId);
                return;
            }
            // Delegate actual JSON generation and file writing to DatapackDataGenerator
            DatapackDataGenerator.generateCookingRecipe(recipeId, recipeType, ingredient, resultId, experience, cookingTime);
        }
    }
}