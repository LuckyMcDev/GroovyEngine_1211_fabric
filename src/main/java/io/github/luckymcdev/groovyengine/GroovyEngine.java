package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.generators.structure.DatapackGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.generators.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventContext;
import io.github.luckymcdev.groovyengine.scripting.eventservice.EventRegistry;
import io.github.luckymcdev.groovyengine.logging.LogCapture;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.GroovyRegisterBlockEvents;
import io.github.luckymcdev.groovyengine.scripting.eventservice.events.GroovyRegisterItemEvents;
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

		LOGGER.info("Generating Pack Structure");
		GroovyEnginePackRootGenerator.generate();
		ResourcepackGenerator.generate();
		DatapackGenerator.generate();

		System.out.println("Loading Scripts");
		GroovyScriptManager.initialize();

		// Init server Events
		EventRegistry.initServer();
		fireRegisterBlockEvent();
		fireRegisterItemEvent();


		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onDataPackReloadEnd);
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);
	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		EventRegistry.clearAllEvents();
		GroovyScriptManager.reloadScripts();

	}

	private void onDataPackReloadEnd(MinecraftServer server, ResourceManager resourceManager, boolean success) {
		if (success) {
			System.out.println("GroovyEngine: Scripts reloaded successfully");
		} else {
			System.err.println("GroovyEngine: Scripts reload failed");
		}
	}


	// --- REGISTRATION EVENTS ---
	private static void fireRegisterItemEvent() {
		EventContext ctx = new EventContext("registerItem");
		GroovyRegisterItemEvents.fire(ctx);
	}

	private static void fireRegisterBlockEvent() {
		EventContext ctx = new EventContext("registerBlock");
		GroovyRegisterBlockEvents.fire(ctx);
	}

}