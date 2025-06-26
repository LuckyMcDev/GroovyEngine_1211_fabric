package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.packs.OpenloaderConfigPatcher;
import io.github.luckymcdev.groovyengine.packs.structure.DatapackGenerator;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.packs.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.logging.LogCapture;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		DatapackGenerator.generate();

		System.out.println("Loading Scripts");
		GroovyScriptManager.initialize();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onDataPackReloadEnd);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);
	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		GroovyScriptManager.reloadScripts();

	}

	private void onDataPackReloadEnd(MinecraftServer server, ResourceManager resourceManager, boolean success) {
		if (success) {
			System.out.println("GroovyEngine: Scripts reloaded successfully");
		} else {
			System.err.println("GroovyEngine: Scripts reload failed");
		}
	}

}