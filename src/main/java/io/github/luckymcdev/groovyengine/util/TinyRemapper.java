package io.github.luckymcdev.groovyengine.util;

import java.io.*;
import java.util.*;

public class TinyRemapper {
    public final Map<String, String> classMap = new HashMap<>();
    public final Map<String, String> fieldMap = new HashMap<>();
    public final Map<String, String> methodMap = new HashMap<>();
    public final Map<String, String> innerClassMap = new HashMap<>();

    public void loadTinyFile(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("tiny")) {
                throw new IOException("Invalid tiny header");
            }

            String line;
            String currentClass = null;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\t");
                switch (p[0]) {
                    case "c" -> {
                        if (p.length < 4) continue;
                        String named = p[1];
                        String obf = p[3];
                        classMap.put(named, obf);
                        currentClass = named;

                        if (named.contains("$")) {
                            String dottedNamed = named.replace('/', '.').replace('$', '.');
                            String dottedObf = obf.replace('/', '.').replace('$', '.');
                            innerClassMap.put(dottedNamed, dottedObf);
                        }
                    }
                    case "f" -> {
                        if (p.length >= 4) {
                            String named = p[2];
                            String obf = p[3];
                            fieldMap.put(named, obf);
                        }
                    }
                    case "m" -> {
                        if (p.length >= 4) {
                            String named = p[2];
                            String obf = p[3];
                            methodMap.put(named, obf);
                        }
                    }
                }
            }
        }
    }

    public String mapClass(String internal) {
        return classMap.getOrDefault(internal, internal);
    }

    public String mapField(String name) {
        return fieldMap.getOrDefault(name, name);
    }

    public String mapMethod(String name) {
        return methodMap.getOrDefault(name, name);
    }

    public Map<String, String> getInnerClasses() {
        return innerClassMap;
    }
}
