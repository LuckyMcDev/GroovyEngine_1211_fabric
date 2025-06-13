package io.github.luckymcdev.groovyengine.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class LangGenerator {

    private static final Path LANG_FILE = FabricLoader.getInstance()
            .getGameDir()
            .resolve("GroovyEngine/data/resourcepacks/GroovyEnginePack/assets/groovyengine/lang/en_us.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Adds or updates a language entry for the given key with the provided display name.
     */
    public static void addLangEntry(String key, String displayName) {
        try {
            JsonObject langJson;

            if (Files.exists(LANG_FILE)) {
                String jsonText = Files.readString(LANG_FILE, StandardCharsets.UTF_8);
                langJson = GSON.fromJson(jsonText, JsonObject.class);
                if (langJson == null) {
                    langJson = new JsonObject();
                }
            } else {
                // If file doesn't exist, create empty JSON object
                langJson = new JsonObject();
            }

            // Put or overwrite the entry
            langJson.addProperty(key, displayName);

            // Write it back to the lang file
            Files.createDirectories(LANG_FILE.getParent()); // ensure parent folders exist
            Files.writeString(LANG_FILE, GSON.toJson(langJson), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts an item registry name (e.g., "banana_split") to a display name ("Banana Split").
     */
    public static String toDisplayName(String registryName) {
        String[] parts = registryName.split("_");
        StringBuilder displayName = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                displayName.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
                if (part.length() > 1) {
                    displayName.append(part.substring(1).toLowerCase(Locale.ROOT));
                }
                displayName.append(' ');
            }
        }
        return displayName.toString().trim();
    }
}
