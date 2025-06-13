package io.github.luckymcdev.groovyengine.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GroovyKeybinds {

    public static KeyBinding openMainEditorKey;

    public static void init() {
        openMainEditorKey = register("open_main_editor", GLFW.GLFW_KEY_F6);
    }

    private static KeyBinding register(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.groovyengine." + name,
                key,
                "key.categories.groovyengine"
        ));
    }
}
