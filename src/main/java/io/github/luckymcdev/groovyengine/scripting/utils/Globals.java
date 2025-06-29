package io.github.luckymcdev.groovyengine.scripting.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Globals {
    private static final Map<String, Object> GLOBALS = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        GLOBALS.put(key, value);
    }

    public static Object get(String key) {
        return GLOBALS.get(key);
    }

    public static <T> T get(String key, Class<T> type) {
        Object value = GLOBALS.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public static boolean has(String key) {
        return GLOBALS.containsKey(key);
    }

    public static void remove(String key) {
        GLOBALS.remove(key);
    }

    public static void clear() {
        GLOBALS.clear();
    }

    public static Map<String, Object> view() {
        return Map.copyOf(GLOBALS); // read-only snapshot
    }
}
