package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.generators.structure.DatapackGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventRegistry;
import io.github.luckymcdev.groovyengine.logging.LogCapture;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyEngine implements ModInitializer {
	public static final String MODID = "groovyengine";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static boolean areShadersDisabled() {
		return false;
    }

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LogCapture.hookLog4j();

		LOGGER.info("Generating Pack Structure");
		GroovyEnginePackRootGenerator.generate();
		ResourcepackGenerator.generate();
		DatapackGenerator.generate();

		System.out.println("Loading Scripts");
		GroovyScriptManager.initialize();

		// Init server Events
		EventRegistry.initServer();


		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onDataPackReloadEnd);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);
	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		EventRegistry.clearAllEvents();
	}

	private void onDataPackReloadEnd(MinecraftServer server, ResourceManager resourceManager, boolean success) {
		if (success) {
			System.out.println("GroovyEngine: Scripts reloaded successfully");
		} else {
			System.err.println("GroovyEngine: Scripts reload failed");
		}
	}

}