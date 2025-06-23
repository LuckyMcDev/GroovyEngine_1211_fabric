package io.github.luckymcdev.groovyengine.scripting.builders.shaders;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShaderRegistry {
    private static final Map<String, ShaderBuilder> shaders = new LinkedHashMap<>();

    public static void register(String id, ShaderBuilder builder) {
        shaders.put(id, builder);
    }

    public static Map<String, ShaderBuilder> getShaders() {
        return shaders;
    }

    public static void toggleShader(String id) {
        ShaderBuilder builder = shaders.get(id);
        if (builder != null) {
            if (builder.isEnabled()) {
                builder.disable();
            } else {
                builder.enable();
            }
        }
    }
}
