package io.github.luckymcdev.api.scripting.input;

import groovy.lang.Closure;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeysBinding {
    public void onPress(String keyName, Closure<?> callback) {
        KeyInputHandler.onPress(keyName, callback);
    }
}
