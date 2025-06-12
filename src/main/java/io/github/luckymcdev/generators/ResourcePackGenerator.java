package io.github.luckymcdev.generators;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcePackGenerator {

    private static final int PACK_FORMAT = 17; // For Minecraft 1.21

    public static void generateResourcePack() {
        Path gameDir = FabricLoader.getInstance().getGameDir();

        // Base folder for the resource packs under your GroovyEngine folder
        Path baseDir = gameDir.resolve("GroovyEngine").resolve("data").resolve("resourcepacks").resolve("GroovyEnginePack");

        try {
            // Create all the needed folders
            Files.createDirectories(baseDir.resolve("assets/groovyengine/textures/item"));
            Files.createDirectories(baseDir.resolve("assets/groovyengine/textures/block"));
            Files.createDirectories(baseDir.resolve("assets/groovyengine/models/item"));
            Files.createDirectories(baseDir.resolve("assets/groovyengine/models/block"));
            Files.createDirectories(baseDir.resolve("assets/groovyengine/lang"));

            // Create pack.mcmeta with minimal content
            String packMcmeta = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": " + PACK_FORMAT + ",\n" +
                    "    \"description\": \"GroovyEngine Resource Pack\"\n" +
                    "  }\n" +
                    "}";
            Files.writeString(baseDir.resolve("pack.mcmeta"), packMcmeta, StandardCharsets.UTF_8);

            // Create or overwrite en_us.json with empty JSON object if it doesn't exist
            Path langFile = baseDir.resolve("assets/groovyengine/lang/en_us.json");
            if (Files.notExists(langFile)) {
                Files.writeString(langFile, "{}", StandardCharsets.UTF_8);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
