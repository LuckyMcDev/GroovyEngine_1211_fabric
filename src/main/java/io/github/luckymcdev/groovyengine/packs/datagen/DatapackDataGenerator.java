package io.github.luckymcdev.groovyengine.packs.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.util.Identifier;

public class DatapackDataGenerator {

    private static final Path DATAPACK_NAMESPACE_ROOT = GroovyEnginePackRootGenerator.DATAPACK_ROOT
            .resolve("data")
            .resolve(GroovyEngine.MODID);

    private static final Path DATAPACK_RECIPES_DIR = DATAPACK_NAMESPACE_ROOT.resolve("recipe");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


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

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        if (count > 1) {
            resultObject.addProperty("count", count);
        }
        recipeJson.add("result", resultObject);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }


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

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        if (count > 1) {
            resultObject.addProperty("count", count);
        }
        recipeJson.add("result", resultObject);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }


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

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", resultId.toString());
        recipeJson.add("result", resultObject);

        recipeJson.addProperty("experience", experience);
        recipeJson.addProperty("cookingtime", cookingTime);

        writeRecipeFile(DATAPACK_RECIPES_DIR, recipeId, recipeJson);
    }

    private static void writeRecipeFile(Path directory, Identifier id, JsonObject json) {
        Path jsonFile = directory.resolve(id.getPath() + ".json");
        try {
            Files.createDirectories(directory);

            String jsonString = GSON.toJson(json);
            GroovyEngine.LOGGER.debug("Attempting to write recipe {}. Generated JSON:\n{}", id, jsonString);

            Files.writeString(jsonFile, jsonString, StandardCharsets.UTF_8);
            GroovyEngine.LOGGER.info("Generated datapack file: {}", id);
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to write datapack file: " + jsonFile, e);
        }
    }
}