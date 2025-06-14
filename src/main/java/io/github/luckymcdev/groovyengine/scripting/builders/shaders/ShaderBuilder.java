package io.github.luckymcdev.groovyengine.scripting.builders.shaders;

import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.util.HashSet;
import java.util.Set;

public class ShaderBuilder {
    private static final Set<ShaderBuilder> REGISTERED_SHADERS = new HashSet<>();
    private static boolean callbackRegistered = false;

    private final Identifier shaderId;
    private ManagedShaderEffect shaderEffect;
    private boolean enabled = false;

    public ShaderBuilder(String id) {
        this.shaderId = Identifier.of(id);
    }

    public ShaderBuilder path(String path) {
        this.shaderEffect = ShaderEffectManager.getInstance().manage(Identifier.of(path));
        return this;
    }

    public ShaderBuilder enable() {
        if (!enabled) {
            enabled = true;
            REGISTERED_SHADERS.add(this);
            ensureCallbackRegistered();
        }
        return this;
    }

    public ShaderBuilder disable() {
        if (enabled) {
            enabled = false;
            REGISTERED_SHADERS.remove(this);
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

    private static void ensureCallbackRegistered() {
        if (!callbackRegistered) {
            ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
                for (ShaderBuilder shader : REGISTERED_SHADERS) {
                    if (shader.enabled && shader.shaderEffect != null) {
                        shader.shaderEffect.render(tickDelta);
                    }
                }
            });
            callbackRegistered = true;
        }
    }

    // Static helper to create builder more fluently
    public static ShaderBuilder create(String id) {
        return new ShaderBuilder(id);
    }
}
