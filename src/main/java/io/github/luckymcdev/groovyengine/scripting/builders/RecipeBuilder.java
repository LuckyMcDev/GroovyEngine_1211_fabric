package io.github.luckymcdev.groovyengine.scripting.builders;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.packs.datagen.DatapackDataGenerator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Identifier;

/**
 * Builder for creating various types of recipes with a fluent API.
 */
public class RecipeBuilder {
    private static final Map<String, AbstractRecipeBuilder<?>> RECIPES = new HashMap<>();

    /**
     * Creates a new shaped recipe builder.
     */
    public static ShapedRecipeBuilder shaped(String id) {
        return new ShapedRecipeBuilder(id);
    }

    /**
     * Creates a new shapeless recipe builder.
     */
    public static ShapelessRecipeBuilder shapeless(String id) {
        return new ShapelessRecipeBuilder(id);
    }

    /**
     * Creates a new smelting recipe builder.
     */
    public static CookingRecipeBuilder smelting(String id) {
        return new CookingRecipeBuilder(id, "minecraft:smelting");
    }

    /**
     * Creates a new blasting recipe builder.
     */
    public static CookingRecipeBuilder blasting(String id) {
        return new CookingRecipeBuilder(id, "minecraft:blasting");
    }

    /**
     * Creates a new smoking recipe builder.
     */
    public static CookingRecipeBuilder smoking(String id) {
        return new CookingRecipeBuilder(id, "minecraft:smoking");
    }

    /**
     * Creates a new campfire recipe builder.
     */
    public static CookingRecipeBuilder campfire(String id) {
        return new CookingRecipeBuilder(id, "minecraft:campfire_cooking");
    }

    /**
     * Gets a registered recipe by ID.
     */
    public static AbstractRecipeBuilder<?> get(String id) {
        return RECIPES.get(id);
    }

    /**
     * Base class for all recipe builders.
     */
    public abstract static class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {
        protected final Identifier recipeId;
        protected Identifier resultId;
        protected int count = 1;

        protected AbstractRecipeBuilder(String id) {
            this.recipeId = Identifier.of(GroovyEngine.MODID, id);
        }

        /**
         * Sets the output item and count.
         */
        public T output(String itemIdentifier, int count) {
            this.resultId = Identifier.of(itemIdentifier);
            this.count = count;
            return (T) this;
        }

        /**
         * Sets the output item with default count (1).
         */
        public T output(String itemIdentifier) {
            return output(itemIdentifier, 1);
        }

        /**
         * Finalizes and generates the recipe.
         */
        public void build() {
            generate();
            RECIPES.put(recipeId.getPath(), this);
        }

        /**
         * Internal recipe generation logic.
         */
        protected abstract void generate();
    }

    /**
     * Builder for shaped recipes.
     */
    public static class ShapedRecipeBuilder extends AbstractRecipeBuilder<ShapedRecipeBuilder> {
        private String[] pattern;
        private final Map<Character, String> key = new HashMap<>();

        public ShapedRecipeBuilder(String id) {
            super(id);
        }

        /**
         * Sets the crafting pattern.
         */
        public ShapedRecipeBuilder pattern(String... pattern) {
            this.pattern = pattern;
            return this;
        }

        /**
         * Maps a symbol to an ingredient.
         */
        public ShapedRecipeBuilder key(String symbol, String itemOrTagIdentifier) {
            if (symbol == null || symbol.length() != 1) {
                GroovyEngine.LOGGER.warn("RecipeBuilder.shaped('{}').key() received invalid symbol: '{}'", recipeId, symbol);
                if (symbol == null || symbol.isEmpty()) return this;
            }
            this.key.put(symbol.charAt(0), itemOrTagIdentifier);
            return this;
        }

        @Override
        protected void generate() {
            if (pattern == null || pattern.length == 0 || key.isEmpty() || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid shaped recipe {}: missing pattern, key, or result", recipeId);
                return;
            }
            DatapackDataGenerator.generateShapedRecipe(recipeId, pattern, key, resultId, count);
        }
    }

    /**
     * Builder for shapeless recipes.
     */
    public static class ShapelessRecipeBuilder extends AbstractRecipeBuilder<ShapelessRecipeBuilder> {
        private String[] ingredients;

        public ShapelessRecipeBuilder(String id) {
            super(id);
        }

        /**
         * Sets the recipe ingredients.
         */
        public ShapelessRecipeBuilder ingredients(String... ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        @Override
        protected void generate() {
            if (ingredients == null || ingredients.length == 0 || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid shapeless recipe {}: missing ingredients or result", recipeId);
                return;
            }
            DatapackDataGenerator.generateShapelessRecipe(recipeId, ingredients, resultId, count);
        }
    }

    /**
     * Builder for cooking recipes (smelting, blasting, etc).
     */
    public static class CookingRecipeBuilder extends AbstractRecipeBuilder<CookingRecipeBuilder> {
        private String ingredient;
        private float experience = 0.0f;
        private int cookingTime = 200;
        private final String recipeType;

        public CookingRecipeBuilder(String id, String type) {
            super(id);
            this.recipeType = type;
        }

        /**
         * Sets the input ingredient.
         */
        public CookingRecipeBuilder ingredient(String itemOrTagIdentifier) {
            this.ingredient = itemOrTagIdentifier;
            return this;
        }

        /**
         * Sets the experience reward.
         */
        public CookingRecipeBuilder xp(float value) {
            this.experience = value;
            return this;
        }

        /**
         * Sets the cooking time in ticks.
         */
        public CookingRecipeBuilder time(int ticks) {
            this.cookingTime = ticks;
            return this;
        }

        @Override
        protected void generate() {
            if (ingredient == null || resultId == null) {
                GroovyEngine.LOGGER.error("Invalid {} recipe {}: missing ingredient or result", recipeType, recipeId);
                return;
            }
            DatapackDataGenerator.generateCookingRecipe(recipeId, recipeType, ingredient, resultId, experience, cookingTime);
        }
    }
}