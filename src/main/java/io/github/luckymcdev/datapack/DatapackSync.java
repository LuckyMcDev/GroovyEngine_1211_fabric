package io.github.luckymcdev.datapack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class DatapackSync {

    public static void syncDatapackToWorld(String modid, Path configDir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getServer() == null) {
            return; // Not in a game/server environment yet
        }

        try {
            Path worldDatapacks = client.getServer().getSavePath(WorldSavePath.DATAPACKS);
            Path sourceDatapack = configDir.resolve("generated_datapack");
            Path targetDatapack = worldDatapacks.resolve(modid + "_datapack");

            // If target doesn't exist or is different, copy
            if (Files.notExists(targetDatapack) || !isSameDatapack(sourceDatapack, targetDatapack)) {
                if (Files.exists(targetDatapack)) {
                    deleteDirectory(targetDatapack);
                }
                copyDirectory(sourceDatapack, targetDatapack);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isSameDatapack(Path source, Path target) throws IOException {
        // Implement your logic:
        // Compare checksums, timestamps, or existence of a marker file
        // Simplest: always return false to overwrite every time (not ideal)
        return false;
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(src -> {
            try {
                Path dest = target.resolve(source.relativize(src));
                if (Files.isDirectory(src)) {
                    if (Files.notExists(dest)) Files.createDirectories(dest);
                } else {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}

