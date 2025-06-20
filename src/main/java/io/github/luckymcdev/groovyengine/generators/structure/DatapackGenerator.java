package io.github.luckymcdev.groovyengine.generators.structure;

import io.github.luckymcdev.groovyengine.GroovyEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatapackGenerator {

    // This path is now relative to the root pack generated by GroovyEnginePackRootGenerator
    private static final Path DATA_ROOT = GroovyEnginePackRootGenerator.DATAPACK_ROOT.getRoot().resolve("data")
            .resolve(GroovyEngine.MODID); // Your mod ID as the namespace

    public static void generate() {
        try {
            // All following folders contain .json files defining the content:
            Files.createDirectories(DATA_ROOT.resolve("advancement"));
            // Files.createDirectories(DATA_ROOT.resolve("banner_pattern")); // Experimental, add if needed
            // Files.createDirectories(DATA_ROOT.resolve("cat_variant")); // Experimental, add if needed
            // Files.createDirectories(DATA_ROOT.resolve("chat_type"));
            // Files.createDirectories(DATA_ROOT.resolve("chicken_variant")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("cow_variant")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("damage_type"));
            // Files.createDirectories(DATA_ROOT.resolve("dialog"));
            // Files.createDirectories(DATA_ROOT.resolve("dimension")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("dimension_type")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("enchantment")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("enchantment_provider"));
            // Files.createDirectories(DATA_ROOT.resolve("frog_variant")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("instrument"));
            // Files.createDirectories(DATA_ROOT.resolve("item_modifier"));
            // Files.createDirectories(DATA_ROOT.resolve("jukebox_song"));
            Files.createDirectories(DATA_ROOT.resolve("loot_table")); // As requested by you earlier
            // Files.createDirectories(DATA_ROOT.resolve("painting_variant"));
            // Files.createDirectories(DATA_ROOT.resolve("pig_variant")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("predicate"));
            Files.createDirectories(DATA_ROOT.resolve("recipe")); // For your recipes!
            // Files.createDirectories(DATA_ROOT.resolve("test_environment")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("test_instance")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("trial_spawner")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("trim_material"));
            // Files.createDirectories(DATA_ROOT.resolve("trim_pattern"));
            // Files.createDirectories(DATA_ROOT.resolve("wolf_sound_variant")); // Experimental
            // Files.createDirectories(DATA_ROOT.resolve("wolf_variant")); // Experimental

            // Examples of worldgen (experimental) - uncomment if you need them:
            // Files.createDirectories(DATA_ROOT.resolve("worldgen/biome"));
            // Files.createDirectories(DATA_ROOT.resolve("worldgen/configured_carver"));

            // Core tags folder structure
            Files.createDirectories(DATA_ROOT.resolve("tags/function"));
            // Files.createDirectories(DATA_ROOT.resolve("tags/block")); // Example tag types
            // Files.createDirectories(DATA_ROOT.resolve("tags/item"));

            GroovyEngine.LOGGER.info("Generated Datapack data folder structure.");

        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to generate Datapack data structure: {}", e.getMessage());
        }
    }
}