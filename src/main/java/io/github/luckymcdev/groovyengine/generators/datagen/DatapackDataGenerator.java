package io.github.luckymcdev.groovyengine.generators.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.generators.structure.GroovyEnginePackRootGenerator;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DatapackDataGenerator {

    private static final Path DATAPACK_NAMESPACE_ROOT = GroovyEnginePackRootGenerator.DATAPACK_ROOT
            .resolve("data")
            .resolve(GroovyEngine.MODID);

    private static final Path DATAPACK_RECIPES_DIR = DATAPACK_NAMESPACE_ROOT.resolve("recipe");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Generates a shaped crafting recipe JSON file.
     *
     * @param recipeId The full identifier for the recipe (e.g., "my_mod:my_recipe").
     * @param pattern  A list of strings representing the crafting grid pattern (e.g., ["AAA", "BBB", "CCC"]).
     * @param key      A map of characters to item/tag identifiers (e.g., {'A': 'minecraft:iron_ingot'}).
     * @param resultId The identifier of the output item (e.g., "minecraft:iron_block").
     * @param count    The number of output items.
     */
    public static void generateShapedRecipe(Identifier recipeId, String[] pattern, Map<Character, String> key, Identifier resultId, int count) {
        JsonObject recipeJson = new JsonObject();
        recipeJson.addProperty("type", "minecraft:crafting_shaped");

        JsonArray patternArray = new JsonArray();
        for (String row : pattern) {
            patternArray.add(row);
        }
        recipeJson.add("pattern", patternArray);

        JsonObject keyObject = new JsonObject();
        for (Map.Entry<Character, String> entry : key.entrySet()) {
            JsonObject itemObject = new JsonObject();
            if (entry.getValue().startsWith("#")) {
                itemObject.addProperty("tag", entry.getValue().substring(1));
            } else {
                itemObject.addProperty("item", entry.getValue());
            }
            keyObject.add(String.valueOf(entry.getKey()), itemObject);
        }
        recipeJson.add("key", keyObject);

        // Explicitly build the result object for shaped recipes
        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        if (count > 1) {
            resultObject.addProperty("count", count);
        }
        recipeJson.add("result", resultObject);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }

    /**
     * Generates a shapeless crafting recipe JSON file.
     *
     * @param recipeId    The full identifier for the recipe.
     * @param ingredients A list of item/tag identifiers (e.g., ["minecraft:diamond", "minecraft:stick"]).
     * @param resultId    The identifier of the output item.
     * @param count       The number of output items.
     */
    public static void generateShapelessRecipe(Identifier recipeId, String[] ingredients, Identifier resultId, int count) {
        JsonObject recipeJson = new JsonObject();
        recipeJson.addProperty("type", "minecraft:crafting_shapeless");

        JsonArray ingredientsArray = new JsonArray();
        for (String ingredient : ingredients) {
            JsonObject itemObject = new JsonObject();
            if (ingredient.startsWith("#")) {
                itemObject.addProperty("tag", ingredient.substring(1));
            } else {
                itemObject.addProperty("item", ingredient);
            }
            ingredientsArray.add(itemObject);
        }
        recipeJson.add("ingredients", ingredientsArray);

        // Explicitly build the result object for shapeless recipes
        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        if (count > 1) {
            resultObject.addProperty("count", count);
        }
        recipeJson.add("result", resultObject);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }

    /**
     * Generates a cooking recipe JSON file (smelting, blasting, smoking, campfire_cooking).
     *
     * @param recipeId    The full identifier for the recipe.
     * @param type        The recipe type (e.g., "minecraft:smelting", "minecraft:blasting").
     * @param ingredient  The item/tag identifier for the input.
     * @param resultId    The identifier of the output item.
     * @param experience  The experience gained.
     * @param cookingTime The time in ticks.
     */
    public static void generateCookingRecipe(Identifier recipeId, String type, String ingredient, Identifier resultId, float experience, int cookingTime) {
        JsonObject recipeJson = new JsonObject();
        recipeJson.addProperty("type", type);

        JsonObject ingredientObject = new JsonObject();
        if (ingredient.startsWith("#")) {
            ingredientObject.addProperty("tag", ingredient.substring(1));
        } else {
            ingredientObject.addProperty("item", ingredient);
        }
        recipeJson.add("ingredient", ingredientObject);

        // Explicitly build the result object for cooking recipes
        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        // For cooking recipes, 'count' property is usually only added if it's > 1.
        // If your builder allows setting count for cooking, you'd add:
        // if (count > 1) { resultObject.addProperty("count", count); }
        // For now, assuming default count of 1.
        recipeJson.add("result", resultObject);

        recipeJson.addProperty("experience", experience);
        recipeJson.addProperty("cookingtime", cookingTime);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }

    private static void writeRecipeFile(Path directory, Identifier id, JsonObject json) {
        Path jsonFile = directory.resolve(id.getPath() + ".json");
        try {
            Files.createDirectories(directory); // Ensure the recipe directory exists just before writing

            String jsonString = GSON.toJson(json); // Capture the JSON string
            // Log the full JSON string to debug exactly what's being written
            GroovyEngine.LOGGER.debug("Attempting to write recipe {}. Generated JSON:\n{}", id, jsonString);

            Files.writeString(jsonFile, jsonString, StandardCharsets.UTF_8);
            GroovyEngine.LOGGER.info("Generated datapack file: {}", id);
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to write datapack file: " + jsonFile, e);
        }
    }
}