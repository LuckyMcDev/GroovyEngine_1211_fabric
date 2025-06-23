package io.github.luckymcdev.groovyengine.packs.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcePackDataGenerator {

    private static final Path RESOURCEPACK_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("GroovyEngine/data/resourcepacks/GroovyEnginePack");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static void generateItemModel(String itemName, String texturePath) {
        try {
            Path modelsDir = RESOURCEPACK_PATH.resolve("assets/groovyengine/models/item");
            Files.createDirectories(modelsDir);

            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("parent", "item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", texturePath);
            modelJson.add("textures", textures);

            Path modelFile = modelsDir.resolve(itemName + ".json");
            Files.writeString(modelFile, GSON.toJson(modelJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateBlockModel(String blockName, String texturePath) {
        try {
            Path modelsDir = RESOURCEPACK_PATH.resolve("assets/groovyengine/models/block");
            Files.createDirectories(modelsDir);

            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("parent", "block/cube_all");

            JsonObject textures = new JsonObject();
            textures.addProperty("all", texturePath);
            modelJson.add("textures", textures);

            Path modelFile = modelsDir.resolve(blockName + ".json");
            Files.writeString(modelFile, GSON.toJson(modelJson));

            Path blockstateDir = RESOURCEPACK_PATH.resolve("assets/groovyengine/blockstates");
            Files.createDirectories(blockstateDir);

            JsonObject blockstateJson = new JsonObject();
            JsonObject variants = new JsonObject();
            JsonObject variantEntry = new JsonObject();
            variantEntry.addProperty("model", GroovyEngine.MODID + ":block/" + blockName);
            variants.add("", variantEntry);
            blockstateJson.add("variants", variants);

            Path blockstateFile = blockstateDir.resolve(blockName + ".json");
            Files.writeString(blockstateFile, GSON.toJson(blockstateJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
