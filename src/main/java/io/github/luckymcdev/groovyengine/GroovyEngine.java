package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.logging.LogCapture;
import io.github.luckymcdev.groovyengine.packs.OpenloaderConfigPatcher;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.packs.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.util.mapping.MappingLocation;
import io.github.luckymcdev.groovyengine.util.mapping.MappingResolver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
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

		MappingResolver resolver = new MappingResolver();
		try {
			resolver.loadAllMappings("/assets/groovyengine/mappings/");
			String obfItemClass = resolver.getObfClass("net.minecraft.item.Item");
			String obfBlockClass = resolver.getObfClass("net.minecraft.block.Block");
			System.out.println("Item Obfuscated class: " + obfItemClass);
			System.out.println("Block Obfuscated class: " + obfBlockClass);
		} catch (IOException e) {
			e.printStackTrace();
		}


		LOGGER.info("Loading Scripts");
		GroovyScriptManager.initialize();
		GroovyScriptManager.reloadScripts();

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);
	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		LOGGER.info("GroovyEngine: Scripts reload is starting");
		GroovyScriptManager.reloadScripts();
	}
}