package io.github.luckymcdev.groovyengine.generators.structure;

import io.github.luckymcdev.groovyengine.GroovyEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcepackGenerator {

    private static final Path ASSETS_ROOT = GroovyEnginePackRootGenerator.RESOURCEPACK_ROOT
            .resolve("assets")
            .resolve(GroovyEngine.MODID);

    public static void generate() {
        try {
            Files.createDirectories(ASSETS_ROOT.resolve("textures/item"));
            Files.createDirectories(ASSETS_ROOT.resolve("textures/block"));
            Files.createDirectories(ASSETS_ROOT.resolve("models/item"));
            Files.createDirectories(ASSETS_ROOT.resolve("models/block"));
            Files.createDirectories(ASSETS_ROOT.resolve("lang"));
            Files.createDirectories(ASSETS_ROOT.resolve("shaders/post"));

            Path langFile = ASSETS_ROOT.resolve("lang/en_us.json");
            if (Files.notExists(langFile)) {
                Files.writeString(langFile, "{}", StandardCharsets.UTF_8);
            }

            GroovyEngine.LOGGER.info("Generated Resource Pack assets folder structure.");

        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to generate Resource Pack assets structure: {}", e.getMessage());
        }
    }
}