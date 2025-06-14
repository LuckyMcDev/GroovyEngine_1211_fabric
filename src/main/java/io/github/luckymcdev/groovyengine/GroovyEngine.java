package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.generators.structure.DatapackGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.rendering.api.event.PostWorldRenderCallback;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptLoader;
import io.github.luckymcdev.groovyengine.script_event.EventContext;
import io.github.luckymcdev.groovyengine.script_event.Events;
import io.github.luckymcdev.groovyengine.script_event.EventRegistry;
import io.github.luckymcdev.groovyengine.logging.LogCapture;
import net.fabricmc.api.ModInitializer;

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
		GroovyScriptLoader.initialize();

		Events.trigger("registerItem", new EventContext("registerItem"));
		Events.trigger("registerBlock", new EventContext("registerBlock"));

		// Initialize all other events like blockBreak, playerJoin etc.
		EventRegistry.initServer();
	}

}