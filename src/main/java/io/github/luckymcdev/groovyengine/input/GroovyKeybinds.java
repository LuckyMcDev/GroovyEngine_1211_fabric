package io.github.luckymcdev.groovyengine.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class GroovyKeybinds {

    public static KeyMapping openMainEditorKey;

    public static void init() {
        openMainEditorKey = register("open_main_editor", GLFW.GLFW_KEY_F6);
    }

    private static KeyMapping register(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.groovyengine." + name,
                key,
                "key.categories.groovyengine"
        ));
    }
}
