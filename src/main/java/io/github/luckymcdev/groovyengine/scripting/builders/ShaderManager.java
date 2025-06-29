package io.github.luckymcdev.groovyengine.scripting.builders;

import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShaderManager {
    private static final Map<String, ShaderBuilder> shaders = new LinkedHashMap<>();
    private static boolean callbackRegistered = false;

    public static class ShaderBuilder {
        private final String id;
        private final Identifier shaderId;
        private ManagedShaderEffect shaderEffect;
        private boolean enabled = false;

        public ShaderBuilder(String id) {
            this.id = id;
            this.shaderId = Identifier.of(id);
        }

        public ShaderBuilder path(String path) {
            this.shaderEffect = ShaderEffectManager.getInstance().manage(Identifier.of(path));
            return this;
        }

        public ShaderBuilder enable() {
            if (!enabled) {
                enabled = true;
                ensureCallbackRegistered();
            }
            return this;
        }

        public ShaderBuilder disable() {
            if (enabled) {
                enabled = false;
            }
            return this;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void build() {
            if (shaderEffect == null) {
                this.shaderEffect = ShaderEffectManager.getInstance().manage(shaderId);
            }
        }

        public void render(float tickDelta) {
            if (enabled && shaderEffect != null) {
                shaderEffect.render(tickDelta);
            }
        }

        public String getId() {
            return id;
        }
    }

    public static ShaderBuilder create(String id) {
        return new ShaderBuilder(id);
    }

    public static void register(String id, ShaderBuilder builder) {
        shaders.put(id, builder);
    }

    public static Map<String, ShaderBuilder> getShaders() {
        return shaders;
    }

    public static void toggle(String id) {
        ShaderBuilder builder = shaders.get(id);
        if (builder != null) {
            if (builder.isEnabled()) {
                builder.disable();
            } else {
                builder.enable();
            }
        }
    }

    private static void ensureCallbackRegistered() {
        if (!callbackRegistered) {
            ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
                for (ShaderBuilder shader : shaders.values()) {
                    shader.render(tickDelta);
                }
            });
            callbackRegistered = true;
        }
    }
}
