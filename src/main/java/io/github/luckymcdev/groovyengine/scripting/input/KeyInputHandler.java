package io.github.luckymcdev.groovyengine.scripting.input;

import com.mojang.blaze3d.platform.InputConstants;
import groovy.lang.Closure;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class KeyInputHandler {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<String, Closure<?>> keyListeners = new HashMap<>();
    private static final Map<String, Boolean> lastKeyState = new HashMap<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            for (String keyName : keyListeners.keySet()) {
                int keyCode = getKeyCodeFromName(keyName);
                if (keyCode == -1) continue;

                boolean isPressed = InputConstants.isKeyDown(mc.getWindow().getWindow(), keyCode);
                boolean wasPressed = lastKeyState.getOrDefault(keyName, false);

                if (isPressed && !wasPressed) {
                    Closure<?> callback = keyListeners.get(keyName);
                    try {
                        callback.call();
                    } catch (Exception e) {
                        e.printStackTrace();
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
        InputConstants.Key key = InputConstants.getKey("key.keyboard." + keyName.toLowerCase());
        if (key == InputConstants.UNKNOWN) {
            return -1;
        }
        return key.getValue();
    }


}
