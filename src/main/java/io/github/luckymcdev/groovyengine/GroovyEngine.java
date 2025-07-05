package io.github.luckymcdev.groovyengine;

import io.github.luckymcdev.groovyengine.logging.LogCapture;
import io.github.luckymcdev.groovyengine.packs.OpenloaderConfigPatcher;
import io.github.luckymcdev.groovyengine.packs.structure.GroovyEnginePackRootGenerator;
import io.github.luckymcdev.groovyengine.packs.structure.ResourcepackGenerator;
import io.github.luckymcdev.groovyengine.scripting.core.GroovyScriptManager;
import io.github.luckymcdev.groovyengine.scripting.core.ScriptParser;
import io.github.luckymcdev.groovyengine.util.mappings.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
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

		MappingsParser parser = new MappingsParser("assets/groovyengine/tiny/mappings.json");

		// lookup a class by its named name:
		MappingsClass rotAxis = parser.getClassMapping("net/minecraft/util/math/RotationAxis");
		String obf = rotAxis.getObfuscated(); // -> "net/minecraft/class_7833"
		LOGGER.info(obf);

		// iterate over fields:
		for (FieldMapping f : rotAxis.getFields()) {
			LOGGER.info(f.getNamed() + " -> " + f.getObfuscated());
		}

		// iterate over methods + params:
		for (MethodMapping m : rotAxis.getMethods()) {
			LOGGER.info("%s%s -> %s%n", m.getNamed(), m.getSignature(), m.getObfuscated());
			for (ParameterMapping p : m.getParameters()) {
				LOGGER.info("  param %d: %s -> %s%n", p.getIndex(), p.getNamed(), p.getObfuscated());
			}
		}

		MappingsParser mappings = new MappingsParser("assets/groovyengine/tiny/mappings.json");
		ScriptParser scriptParser = new ScriptParser(mappings);

		String originalScript = """
		import net.minecraft.util.math.RotationAxis;
		
		def test() {
			println(RotationAxis.POSITIVE_X);
		}
		""";

		String remapped = scriptParser.remapScript(originalScript);
		LOGGER.warn(remapped);

		LOGGER.info("Loading Scripts");
		GroovyScriptManager.initialize();

		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(this::onDataPackReloadStart);

	}

	private void onDataPackReloadStart(MinecraftServer server, ResourceManager resourceManager) {
		System.out.println("GroovyEngine: Scripts reload is starting");
		GroovyScriptManager.reloadScripts();

	}

}