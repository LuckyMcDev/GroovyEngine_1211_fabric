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
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class GroovyEngine implements ModInitializer {
	public static final String MODID = "groovyengine";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
	public void onInitialize() {
		LogCapture.hookLog4j();

		TinyRemapper tinyRemapper = new TinyRemapper();
		try {
			InputStream tinyStream = this.getClass().getResourceAsStream("/assets/groovyengine/tiny/mappings.tiny");
			if (tinyStream == null) {
				throw new RuntimeException("Could not find mappings.tiny in resources");
			}
			tinyRemapper.loadTinyFile(tinyStream);

			// Set the remapper for the ScriptRunner to use
			ScriptRunner.setTinyRemapper(tinyRemapper);

		} catch (IOException e) {
			throw new RuntimeException("Failed to load tiny mappings", e);
		}

		OpenloaderConfigPatcher.patch();

		LOGGER.info("Generating Pack Structure");
		GroovyEnginePackRootGenerator.generate();
		ResourcepackGenerator.generate();

		System.out.println("Loading Scripts");
		GroovyScriptManager.initialize();

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);

	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		GroovyScriptManager.reloadScripts();

	}

}