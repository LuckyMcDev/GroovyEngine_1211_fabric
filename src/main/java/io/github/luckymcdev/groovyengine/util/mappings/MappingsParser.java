package io.github.luckymcdev.groovyengine.util.mappings;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads a tiny2json type mapping file and exposes lookups for classes, fields, and methods.
 */
public class MappingsParser {
    private final Map<String, MappingsClass> classes = new HashMap<>();

    /**
     * @param pathInJar A Location where the mappings.json file is located
     */
    public MappingsParser(String pathInJar) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(pathInJar)) {
            if (in == null) {
                throw new IllegalArgumentException("Could not find mapping resource: " + pathInJar);
            }
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray children = root.getAsJsonArray("children");

            for (JsonElement e : children) {
                JsonObject node = e.getAsJsonObject();
                if ("c".equals(node.get("tag").getAsString())) {
                    MappingsClass mc = parseClass(node);
                    classes.put(mc.getNamed(), mc);
                    classes.put(mc.getObfuscated(), mc);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mappings from " + pathInJar, e);
        }
    }

    /** Returns null if not found. */
    public MappingsClass getClassMapping(String namedOrObf) {
        return classes.get(namedOrObf);
    }

    private MappingsClass parseClass(JsonObject obj) {
        JsonArray f = obj.getAsJsonArray("fields");

        String namedClass, intermediary, obfClass;
        if (f.size() == 3) {
            // fields = [ named, <junk?>, obf ]
            namedClass   = f.get(0).getAsString();
            obfClass     = f.get(2).getAsString();
            intermediary = obfClass; // fallback to obf if no true intermediary
        } else if (f.size() == 4) {
            // fields = [ named, intermediary, obf, ??? ]
            namedClass   = f.get(0).getAsString();
            intermediary = f.get(1).getAsString();
            obfClass     = f.get(2).getAsString();
        } else {
            throw new IllegalStateException("Unexpected class field count: " + f.size());
        }

        MappingsClass mc = new MappingsClass(namedClass, intermediary, obfClass);

        for (JsonElement child : obj.getAsJsonArray("children")) {
            JsonObject c = child.getAsJsonObject();
            String tag = c.get("tag").getAsString();
            JsonArray arr = c.getAsJsonArray("fields");

            switch (tag) {
                case "f":
                    // f: [ descriptor, named, intermediary, obf ]
                    mc.getFields().add(new FieldMapping(
                            arr.get(0).getAsString(),
                            arr.get(1).getAsString(),
                            arr.get(2).getAsString(),
                            arr.get(3).getAsString()
                    ));
                    break;
                case "m":
                    // m: [ signature, named, intermediary, obf ]
                    MethodMapping mm = new MethodMapping(
                            arr.get(0).getAsString(),
                            arr.get(1).getAsString(),
                            arr.get(2).getAsString(),
                            arr.get(3).getAsString()
                    );
                    // parameters are its children
                    for (JsonElement p : c.getAsJsonArray("children")) {
                        JsonObject po = p.getAsJsonObject();
                        if ("p".equals(po.get("tag").getAsString())) {
                            JsonArray pa = po.getAsJsonArray("fields");
                            // p: [ index, named, obf ]
                            mm.getParameters().add(new ParameterMapping(
                                    pa.get(0).getAsInt(),
                                    pa.get(1).getAsString(),
                                    pa.get(2).getAsString()
                            ));
                        }
                    }
                    mc.getMethods().add(mm);
                    break;
                default:
                    // ignore 'tiny', other tags…
            }
        }

        return mc;
    }
}
