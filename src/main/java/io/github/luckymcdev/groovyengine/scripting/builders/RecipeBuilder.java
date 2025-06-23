package io.github.luckymcdev.groovyengine.scripting.builders;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.generators.datagen.DatapackDataGenerator;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RecipeBuilder {

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


    public abstract static class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {
        protected Identifier recipeId;
        protected Identifier resultId;
        protected int count = 1;

        public AbstractRecipeBuilder(String id) {
            this.recipeId = Identifier.of(GroovyEngine.MODID, id);
        }

        public T output(String itemIdentifier, int count) {
            this.resultId = Identifier.of(itemIdentifier);
            this.count = count;
            return (T) this;
        }

        public T output(String itemIdentifier) {
            return output(itemIdentifier, 1);
        }

        public abstract void buildAndGenerate();
    }

    public static class ShapedRecipeBuilder extends AbstractRecipeBuilder<ShapedRecipeBuilder> {
        private String[] pattern;
        private Map<Character, String> key = new HashMap<>();

        public ShapedRecipeBuilder(String id) {
            super(id);
        }

        public ShapedRecipeBuilder pattern(String... pattern) {
            this.pattern = pattern;
            return this;
        }

        public ShapedRecipeBuilder key(String symbolString, String itemOrTagIdentifier) {

            if (symbolString == null || symbolString.length() != 1) {
                GroovyEngine.LOGGER.warn("RecipeBuilder.shaped('{}').key() received a symbol that is not a single character: '{}'. " +
                        "This may lead to unexpected behavior. Only the first character will be used if valid.", recipeId, symbolString);

                if (symbolString == null || symbolString.isEmpty()) {
                    return this;
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

            DatapackDataGenerator.generateShapedRecipe(recipeId, pattern, key, resultId, count);
        }
    }

    public static class ShapelessRecipeBuilder extends AbstractRecipeBuilder<ShapelessRecipeBuilder> {
        private String[] ingredients;

        public ShapelessRecipeBuilder(String id) {
            super(id);
        }

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

            DatapackDataGenerator.generateShapelessRecipe(recipeId, ingredients, resultId, count);
        }
    }

    public static class CookingRecipeBuilder extends AbstractRecipeBuilder<CookingRecipeBuilder> {
        private String ingredient;
        private float experience = 0.0f;
        private int cookingTime = 200;

        private String recipeType;

        public CookingRecipeBuilder(String id, String type) {
            super(id);
            this.recipeType = type;
        }


        public CookingRecipeBuilder ingredient(String itemOrTagIdentifier) {
            this.ingredient = itemOrTagIdentifier;
            return this;
        }


        public CookingRecipeBuilder xp(float value) {
            this.experience = value;
            return this;
        }


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

            DatapackDataGenerator.generateCookingRecipe(recipeId, recipeType, ingredient, resultId, experience, cookingTime);
        }
    }
}