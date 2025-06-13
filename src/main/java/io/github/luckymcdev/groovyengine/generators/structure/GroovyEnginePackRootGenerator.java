package io.github.luckymcdev.groovyengine.generators.structure;

import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GroovyEnginePackRootGenerator {

    private static final int PACK_FORMAT = 17; // For Minecraft 1.21.1

    public static final Path GROOVY_ENGINE_PACK_ROOT = FabricLoader.getInstance()
            .getGameDir()
            .resolve("GroovyEngine")
            .resolve("data") // This is where the custom packs should generally reside
            .resolve("datapacks") // Datapacks go here
            .resolve("GroovyEnginePack"); // The specific folder name for your combined pack

    public static void generate() {
        try {
            // Create the root directory for the combined pack
            Files.createDirectories(GROOVY_ENGINE_PACK_ROOT);

            // Create pack.mcmeta for the combined pack
            String packMcmeta = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": " + PACK_FORMAT + ",\n" +
                    "    \"description\": \"GroovyEngine Generated Content (Resource Pack & Datapack)\"\n" +
                    "  }\n" +
                    "}";
            Files.writeString(GROOVY_ENGINE_PACK_ROOT.resolve("pack.mcmeta"), packMcmeta, StandardCharsets.UTF_8);

            GroovyEngine.LOGGER.info("Generated base GroovyEnginePack folder and pack.mcmeta.");

        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to generate GroovyEnginePack root or pack.mcmeta: {}", e.getMessage());
        }
    }
}