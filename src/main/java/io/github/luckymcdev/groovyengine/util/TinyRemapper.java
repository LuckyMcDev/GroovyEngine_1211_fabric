package io.github.luckymcdev.groovyengine.util;

import java.io.*;
import java.util.*;

public class TinyRemapper {
    private Map<String, String> namedToObfuscated = new HashMap<>();
    private Map<String, String> obfuscatedToNamed = new HashMap<>();

    public void loadTinyFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("c\t")) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 4) {
                        String named = parts[1];        // first part
                        String intermediary = parts[2]; // weird part
                        String obfuscated = parts[3];   // obfuscated part

                        namedToObfuscated.put(named, obfuscated);
                        obfuscatedToNamed.put(obfuscated, named);
                    } else if (parts.length >= 3) {
                        String first = parts[1];
                        String second = parts[2];
                        if (first.contains("/") && !first.startsWith("class_")) {
                            namedToObfuscated.put(first, second);
                            obfuscatedToNamed.put(second, first);
                        } else {
                            namedToObfuscated.put(second, first);
                            obfuscatedToNamed.put(first, second);
                        }
                    }
                }
            }
        }
    }

    public String mapToObfuscated(String namedClass) {
        return namedToObfuscated.getOrDefault(namedClass, namedClass);
    }

    public String mapToNamed(String obfuscatedClass) {
        return obfuscatedToNamed.getOrDefault(obfuscatedClass, obfuscatedClass);
    }
}
