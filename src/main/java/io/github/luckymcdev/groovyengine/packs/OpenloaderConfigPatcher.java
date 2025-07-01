package io.github.luckymcdev.groovyengine.packs;

import com.google.gson.*;
import io.github.luckymcdev.groovyengine.GroovyEngine;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenloaderConfigPatcher {

    private static final Path CONFIG_PATH = Paths.get("config", "openloader", "options.json");
    private static final List<String> GENGINE_LOCATIONS = List.of(
            "GroovyEngine/data/resourcepacks",
            "GroovyEngine/data/datapacks"
    );

    public static void patch() {
        try {
            // Ensure parent folders exist
            Files.createDirectories(CONFIG_PATH.getParent());

            JsonObject config = loadOrCreateConfig();
            JsonObject additional = getOrCreateObject(config, "additional_locations");
            JsonArray value = getOrCreateArray(additional, "value");

            Set<String> existing = new HashSet<>();
            value.forEach(e -> existing.add(e.getAsString()));

            for (String loc : GENGINE_LOCATIONS) {
                if (!existing.contains(loc)) {
                    value.add(loc);
                }
            }

            // Write updated config
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(config, writer);
            }

            GroovyEngine.LOGGER.info("[GroovyEngine] Patched the OpenLoader config successfully!");

        } catch (IOException e) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] Failed to patch OpenLoader config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JsonObject loadOrCreateConfig() throws IOException {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } else {
            return new JsonObject();
        }
    }

    private static JsonObject getOrCreateObject(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        }
        return parent.getAsJsonObject(key);
    }

    private static JsonArray getOrCreateArray(JsonObject parent, String key) {
        if (!parent.has(key) || !parent.get(key).isJsonArray()) {
            JsonArray arr = new JsonArray();
            parent.add(key, arr);
            return arr;
        }
        return parent.getAsJsonArray(key);
    }
}
