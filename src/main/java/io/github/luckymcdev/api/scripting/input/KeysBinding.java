package io.github.luckymcdev.api.scripting.input;

import groovy.lang.Closure;

public class KeysBinding {
    public void onPress(String keyName, Closure<?> callback) {
        KeyInputHandler.onPress(keyName, callback);
    }
}
