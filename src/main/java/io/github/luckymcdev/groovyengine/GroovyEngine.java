package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.logging.LogCapture;
import io.github.luckymcdev.groovyengine.packs.OpenloaderConfigPatcher;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.packs.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.scripting.core.ScriptRunner;
import io.github.luckymcdev.groovyengine.util.TinyRemapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;


public class GroovyEngine implements ModInitializer {
	public static final String MODID = "groovyengine";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
	public void onInitialize() {
		LogCapture.hookLog4j();

		OpenloaderConfigPatcher.patch();

		LOGGER.info("Generating Pack Structure");
		GroovyEnginePackRootGenerator.generate();
		ResourcepackGenerator.generate();

		try (InputStream stream = GroovyEngine.class.getResourceAsStream("/assets/groovyengine/tiny/mappings.tiny")) {
			if (stream == null) {
				throw new RuntimeException("mappings.tiny not found in assets");
			}

			TinyRemapper remapper = new TinyRemapper();
			remapper.loadTinyFile(stream);

			LOGGER.error(remapper.mapClass("net/minecraft/item/Item"));

			ScriptRunner.setTinyRemapper(remapper);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load tiny mappings", e);
		}

		System.out.println("Loading Scripts");
		GroovyScriptManager.initialize();

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);

	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		GroovyScriptManager.reloadScripts();

	}

}