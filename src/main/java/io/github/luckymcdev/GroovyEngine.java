package io.github.luckymcdev;

import io.github.luckymcdev.api.scripting.GroovyScriptLoader;
import io.github.luckymcdev.api.scripting.event.EventContext;
import io.github.luckymcdev.api.scripting.event.Events;
import io.github.luckymcdev.api.scripting.event.EventRegistry;
import io.github.luckymcdev.datapack.DatapackGenerator;
import io.github.luckymcdev.datapack.DatapackSync;
import io.github.luckymcdev.api.logging.LogCapture;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class GroovyEngine implements ModInitializer {
	public static final String MODID = "groovyengine";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LogCapture.hookLog4j();

		LOGGER.info("Hello Fabric world!");

		System.out.println("Loading Scripts");
		GroovyScriptLoader.initialize();

		Events.trigger("registerItem", new EventContext("registerItem"));
		Events.trigger("registerBlock", new EventContext("registerBlock"));

		// Initialize all other events like blockBreak, playerJoin etc.
		EventRegistry.init();

		Path configDir = FabricLoader.getInstance().getConfigDir();
		try {
			DatapackGenerator.generateDatapack(configDir, MODID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			DatapackSync.syncDatapackToWorld(MODID, configDir);
		});



	}



}