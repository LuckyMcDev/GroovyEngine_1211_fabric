package io.github.luckymcdev.groovyengine.scripting.builders;

import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

/**
 * Manager for creating and controlling shader effects with builder pattern.
 */
public class ShaderManager {
    private static final Map<String, ShaderBuilder> SHADERS = new LinkedHashMap<>();
    private static boolean callbackRegistered = false;

    /**
     * Builder for individual shader effects.
     */
    public static class ShaderBuilder {
        private final String id;
        private final ResourceLocation shaderId;
        private ManagedShaderEffect shaderEffect;
        private boolean enabled = false;

        private ShaderBuilder(String id) {
            this.id = id;
            this.shaderId = ResourceLocation.parse(id);
        }

        /**
         * Sets the shader resource path.
         * @param path The resource location path (e.g. "shaders/post/bloom.json")
         * @return This builder
         */
        public ShaderBuilder path(String path) {
            this.shaderEffect = ShaderEffectManager.getInstance().manage(ResourceLocation.parse(path));
            return this;
        }

        /**
         * Enables the shader effect.
         * @return This builder
         */
        public ShaderBuilder enable() {
            if (!enabled) {
                enabled = true;
                ensureCallbackRegistered();
            }
            return this;
        }

        /**
         * Disables the shader effect.
         * @return This builder
         */
        public ShaderBuilder disable() {
            if (enabled) {
                enabled = false;
            }
            return this;
        }

        /**
         * Finalizes and registers the shader.
         */
        public void build() {
            if (shaderEffect == null) {
                this.shaderEffect = ShaderEffectManager.getInstance().manage(shaderId);
            }
            SHADERS.put(id, this);
        }

        /**
         * @return Whether this shader is currently enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * @return The ID of this shader
         */
        public String getId() {
            return id;
        }

        /* Internal rendering method */
        private void render(float tickDelta) {
            if (enabled && shaderEffect != null) {
                shaderEffect.render(tickDelta);
            }
        }
    }

    /**
     * Creates a new shader builder instance.
     * @param id Unique identifier for the shader
     * @return New ShaderBuilder instance
     */
    public static ShaderBuilder register(String id) {
        return new ShaderBuilder(id);
    }

    /**
     * Gets a registered shader by ID.
     * @param id The shader ID
     * @return ShaderBuilder or null if not found
     */
    public static ShaderBuilder get(String id) {
        return SHADERS.get(id);
    }

    /**
     * Toggles a shader's enabled state.
     * @param id The shader ID
     */
    public static void toggle(String id) {
        ShaderBuilder builder = SHADERS.get(id);
        if (builder != null) {
            if (builder.isEnabled()) {
                builder.disable();
            } else {
                builder.enable();
            }
        }
    }

    /**
     * @return Map of all registered shaders
     */
    public static Map<String, ShaderBuilder> getShaders() {
        return SHADERS;
    }

    /* Ensures render callback is registered */
    private static void ensureCallbackRegistered() {
        if (!callbackRegistered) {
            ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
                for (ShaderBuilder shader : SHADERS.values()) {
                    shader.render(tickDelta);
                }
            });
            callbackRegistered = true;
        }
    }
}