package io.github.luckymcdev.api.scripting.input;

import io.github.luckymcdev.api.scripting.event.Events;
import groovy.lang.Closure;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyInputHandler {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Map<String, Closure<?>> keyListeners = new HashMap<>();
    private static final Map<String, Boolean> lastKeyState = new HashMap<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            for (String keyName : keyListeners.keySet()) {
                int keyCode = getKeyCodeFromName(keyName);
                if (keyCode == -1) continue;

                boolean isPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyCode);
                boolean wasPressed = lastKeyState.getOrDefault(keyName, false);

                if (isPressed && !wasPressed) { // Key just pressed
                    Closure<?> callback = keyListeners.get(keyName);
                    try {
                        callback.call();
                    } catch (Exception e) {
                        // Log or ignore
                    }
                }

                lastKeyState.put(keyName, isPressed);
            }
        });
    }

    public static void onPress(String keyName, Closure<?> callback) {
        keyListeners.put(keyName.toUpperCase(), callback);
        lastKeyState.put(keyName.toUpperCase(), false);
    }

    private static int getKeyCodeFromName(String keyName) {
        InputUtil.Key key = InputUtil.fromTranslationKey("key.keyboard." + keyName.toLowerCase());
        if (key == InputUtil.UNKNOWN_KEY) {
            return -1;
        }
        return key.getCode();
    }


}
