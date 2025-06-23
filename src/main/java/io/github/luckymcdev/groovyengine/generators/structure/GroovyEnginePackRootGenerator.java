package io.github.luckymcdev.groovyengine.generators.structure;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyEnginePackRootGenerator {

    private static final int DATAPACK_FORMAT = 36;
    private static final int RESOURCEPACK_FORMAT = 26;

    public static final Path BASE_PATH = FabricLoader.getInstance()
            .getGameDir()
            .resolve("GroovyEngine")
            .resolve("data");

    public static final Path DATAPACK_ROOT = BASE_PATH.resolve("datapacks").resolve("GroovyEnginePack");
    public static final Path RESOURCEPACK_ROOT = BASE_PATH.resolve("resourcepacks").resolve("GroovyEnginePack");

    public static void generate() {
        try {
            // Create datapack
            Files.createDirectories(DATAPACK_ROOT);
            String dpMeta = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": " + DATAPACK_FORMAT + ",\n" +
                    "    \"description\": \"GroovyEngine Datapack\"\n" +
                    "  }\n" +
                    "}";
            Files.writeString(DATAPACK_ROOT.resolve("pack.mcmeta"), dpMeta, StandardCharsets.UTF_8);

            // Create resourcepack
            Files.createDirectories(RESOURCEPACK_ROOT);
            String rpMeta = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": " + RESOURCEPACK_FORMAT + ",\n" +
                    "    \"description\": \"GroovyEngine Resourcepack\"\n" +
                    "  }\n" +
                    "}";
            Files.writeString(RESOURCEPACK_ROOT.resolve("pack.mcmeta"), rpMeta, StandardCharsets.UTF_8);

            GroovyEngine.LOGGER.info("Generated base GroovyEngine Datapack and Resourcepack structure.");

        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to generate GroovyEngine packs: {}", e.getMessage());
        }
    }
}
