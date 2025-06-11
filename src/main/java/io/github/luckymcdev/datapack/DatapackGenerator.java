package io.github.luckymcdev.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DatapackGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void generateDatapack(Path configFolder, String modid) throws IOException {
        Path datapackRoot = configFolder.resolve("generated_datapack");
        Path dataFolder = datapackRoot.resolve("data").resolve(modid);
        Path langFolder = dataFolder.resolve("lang");

        // Create directories if not exist
        Files.createDirectories(langFolder);

        // Write pack.mcmeta
        Path packMcmeta = datapackRoot.resolve("pack.mcmeta");
        String packMcmetaJson = """
            {
              "pack": {
                "pack_format": 15,
                "description": "Generated datapack for %s"
              }
            }
            """.formatted(modid);
        Files.writeString(packMcmeta, packMcmetaJson);

        // Create example lang file (en_us.json)
        Path enUsLang = langFolder.resolve("en_us.json");

        Map<String, String> langEntries = new HashMap<>();
        langEntries.put("item." + modid + ".cool_item", "Cool Item");
        langEntries.put("block." + modid + ".cool_block", "Cool Block");

        // Serialize to JSON and save
        try (Writer writer = new FileWriter(enUsLang.toFile())) {
            GSON.toJson(langEntries, writer);
        }
    }
}
