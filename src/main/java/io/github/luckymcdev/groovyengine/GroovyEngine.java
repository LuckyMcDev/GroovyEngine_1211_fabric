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

		MappingLocation location = MappingLocation.of("net.minecraft.item", "Item");
		try (InputStream stream = location.openStream()) {
			MappingResolver resolver = new MappingResolver();
			resolver.load(stream);

			// Test regular class mapping
			String obfClass = resolver.getObfClass("net.minecraft.item.Item");
			System.out.println("Item Obfuscated class: " + obfClass);

			// Test field mapping
			String obfField = resolver.getObfField("net.minecraft.item.Item", "recipeRemainder");
			System.out.println("Item.recipeRemainder Obfuscated field: " + obfField);

			// Test method mapping
			String obfMethod = resolver.getObfMethod("net.minecraft.item.Item", "getTranslationKey");
			System.out.println("Item.getTranslationKey Obfuscated method: " + obfMethod);

			// Test nested class mapping
			String obfNestedClass = resolver.getObfNestedClass("net.minecraft.item.Item", "Settings");
			System.out.println("Item.Settings Obfuscated nested class: " + obfNestedClass);

			// Test full nested class name
			String obfNestedClassFull = resolver.getObfNestedClassFull("net.minecraft.item.Item", "Settings");
			System.out.println("Item.Settings Full obfuscated class: " + obfNestedClassFull);

			// Test convenience method
			String resolvedSettings = resolver.resolveClass("net.minecraft.item.Item$Settings");
			System.out.println("Item$Settings resolved: " + resolvedSettings);

			// Test nested class methods and fields
			String settingsObfMethod = resolver.getObfMethod("net.minecraft.item.Item$Settings", "maxCount");
			System.out.println("Item.Settings.maxCount Obfuscated method: " + settingsObfMethod);

			String settingsObfField = resolver.getObfField("net.minecraft.item.Item$Settings", "recipeRemainder");
			System.out.println("Item.Settings.recipeRemainder Obfuscated field: " + settingsObfField);

			// Print all mappings for debugging
			System.out.println("\n=== DEBUG INFO ===");
			resolver.printLoadedMappings("net.minecraft.item.Item");
			resolver.printLoadedMappings("net.minecraft.item.Item$Settings");

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