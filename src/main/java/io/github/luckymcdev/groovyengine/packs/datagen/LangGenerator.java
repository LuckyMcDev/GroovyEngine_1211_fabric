package io.github.luckymcdev.groovyengine.packs.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class LangGenerator {

    private static final Path LANG_FILE = GroovyEnginePackRootGenerator.RESOURCEPACK_ROOT
            .resolve("assets")
            .resolve("groovyengine")
            .resolve("lang")
            .resolve("en_us.json");


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
                langJson = new JsonObject();
            }

            langJson.addProperty(key, displayName);

            Files.createDirectories(LANG_FILE.getParent()); // ensure parent folders exist
            Files.writeString(LANG_FILE, GSON.toJson(langJson), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
