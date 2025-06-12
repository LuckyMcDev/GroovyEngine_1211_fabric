package io.github.luckymcdev.api.scripting;

import io.github.luckymcdev.GroovyEngine;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptWatcher {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static volatile boolean watching = false;

    public static void startWatching(Path scriptsRoot, Runnable onReload) {
        if (watching) return;
        watching = true;

        executor.submit(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                scriptsRoot.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);

                GroovyEngine.LOGGER.info("ScriptWatcher started");

                while (watching) {
                    WatchKey key = watchService.take();
                    boolean changed = false;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind != StandardWatchEventKinds.OVERFLOW) {
                            changed = true;
                        }
                    }

                    if (changed) {
                        GroovyEngine.LOGGER.info("Detected script change, reloading...");
                        onReload.run();
                    }

                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                GroovyEngine.LOGGER.error("ScriptWatcher failed: " + e.getMessage());
            }
        });
    }

    public static void stopWatching() {
        watching = false;
        executor.shutdownNow();
    }
}
