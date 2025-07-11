package io.github.luckymcdev.groovyengine.util.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MappingResolver {
    private final Map<String, String> classMap = new HashMap<>();
    private final Map<String, Map<String, String>> methodMap = new HashMap<>();
    private final Map<String, Map<String, String>> fieldMap = new HashMap<>();
    private final Map<String, Map<String, String>> nestedClassMap = new HashMap<>();

    public void loadAllMappings(String baseResourcePath) throws IOException {
        // List all mapping files in the base resource path
        String[] mappingFiles = {
                "net/minecraft/item/Item",
                "net/minecraft/block/Block",
                // Add other paths to mapping files here
        };

        for (String filePath : mappingFiles) {
            String resourcePath = baseResourcePath + filePath + ".mapping";
            try (InputStream in = MappingResolver.class.getResourceAsStream(resourcePath)) {
                if (in != null) {
                    load(in);
                } else {
                    System.err.println("Mapping file not found: " + resourcePath);
                }
            }
        }
    }

    public void load(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        String currentObfClass = null;
        String currentDeobfClass = null;
        String currentNestedObfClass = null;
        String currentNestedDeobfClass = null;

        while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("COMMENT") || trimmedLine.startsWith("ARG")) {
                continue;
            }

            int indentLevel = 0;
            for (char c : line.toCharArray()) {
                if (c == '\t') {
                    indentLevel++;
                } else if (c == ' ') {
                    indentLevel += 0.25;
                } else {
                    break;
                }
            }

            if (trimmedLine.startsWith("CLASS ")) {
                String[] parts = trimmedLine.split("\\s+");
                if (parts.length >= 3) {
                    String obfName = parts[1];
                    String deobfName = parts[2];
                    if (indentLevel == 0) {
                        currentObfClass = obfName.replace('/', '.');
                        currentDeobfClass = deobfName.replace('/', '.');
                        currentNestedObfClass = null;
                        currentNestedDeobfClass = null;
                        classMap.put(currentDeobfClass, currentObfClass);
                        fieldMap.put(currentDeobfClass, new HashMap<>());
                        methodMap.put(currentDeobfClass, new HashMap<>());
                    } else if (indentLevel >= 1 && currentDeobfClass != null) {
                        currentNestedObfClass = obfName;
                        currentNestedDeobfClass = deobfName;
                        String fullNestedDeobf = currentDeobfClass + "$" + currentNestedDeobfClass;
                        String fullNestedObf = currentObfClass + "$" + currentNestedObfClass;
                        nestedClassMap.computeIfAbsent(currentDeobfClass, k -> new HashMap<>())
                                .put(currentNestedDeobfClass, currentNestedObfClass);
                        classMap.put(fullNestedDeobf, fullNestedObf);
                        fieldMap.put(fullNestedDeobf, new HashMap<>());
                        methodMap.put(fullNestedDeobf, new HashMap<>());
                    }
                }
            } else if (trimmedLine.startsWith("FIELD ")) {
                String[] parts = trimmedLine.split("\\s+");
                if (parts.length >= 3) {
                    String obfName = parts[1];
                    String deobfName = parts[2];
                    if (indentLevel >= 1 && currentNestedDeobfClass != null) {
                        String fullNestedDeobf = currentDeobfClass + "$" + currentNestedDeobfClass;
                        Map<String, String> fields = fieldMap.get(fullNestedDeobf);
                        if (fields != null) {
                            fields.put(deobfName, obfName);
                        }
                    } else if (currentDeobfClass != null) {
                        Map<String, String> fields = fieldMap.get(currentDeobfClass);
                        if (fields != null) {
                            fields.put(deobfName, obfName);
                        }
                    }
                }
            } else if (trimmedLine.startsWith("METHOD ")) {
                String[] parts = trimmedLine.split("\\s+");
                if (parts.length >= 2) {
                    String obfName = parts[1];
                    String deobfName = null;
                    if (parts.length >= 3) {
                        deobfName = parts[2];
                        if (deobfName.equals("<init>")) {
                            deobfName = "<init>";
                        } else if (deobfName.contains("(")) {
                            deobfName = deobfName.substring(0, deobfName.indexOf('('));
                        }
                    }
                    if (deobfName != null && !deobfName.isEmpty()) {
                        if (indentLevel >= 1 && currentNestedDeobfClass != null) {
                            String fullNestedDeobf = currentDeobfClass + "$" + currentNestedDeobfClass;
                            Map<String, String> methods = methodMap.get(fullNestedDeobf);
                            if (methods != null) {
                                methods.put(deobfName, obfName);
                            }
                        } else if (currentDeobfClass != null) {
                            Map<String, String> methods = methodMap.get(currentDeobfClass);
                            if (methods != null) {
                                methods.put(deobfName, obfName);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getObfClass(String deobfClass) {
        return classMap.get(deobfClass);
    }

    public String getObfField(String deobfClass, String fieldName) {
        Map<String, String> fields = fieldMap.get(deobfClass);
        return fields != null ? fields.get(fieldName) : null;
    }

    public String getObfMethod(String deobfClass, String methodName) {
        Map<String, String> methods = methodMap.get(deobfClass);
        return methods != null ? methods.get(methodName) : null;
    }

    public String getObfNestedClass(String parentDeobfClass, String nestedClassName) {
        Map<String, String> nestedClasses = nestedClassMap.get(parentDeobfClass);
        return nestedClasses != null ? nestedClasses.get(nestedClassName) : null;
    }

    public String getObfNestedClassFull(String parentDeobfClass, String nestedClassName) {
        String parentObf = getObfClass(parentDeobfClass);
        String nestedObf = getObfNestedClass(parentDeobfClass, nestedClassName);
        if (parentObf != null && nestedObf != null) {
            return parentObf + "$" + nestedObf;
        }
        String fullNestedDeobf = parentDeobfClass + "$" + nestedClassName;
        String directMapping = getObfClass(fullNestedDeobf);
        if (directMapping != null) {
            return directMapping;
        }
        return null;
    }

    public String resolveClass(String fullClassName) {
        if (fullClassName.contains("$")) {
            String[] parts = fullClassName.split("\\$");
            if (parts.length == 2) {
                return getObfNestedClassFull(parts[0], parts[1]);
            }
        }
        return getObfClass(fullClassName);
    }

    public void printLoadedMappings(String deobfClass) {
        System.out.println("=== Mappings for " + deobfClass + " ===");
        System.out.println("Obfuscated class: " + getObfClass(deobfClass));
        Map<String, String> fields = fieldMap.get(deobfClass);
        if (fields != null && !fields.isEmpty()) {
            System.out.println("Fields:");
            fields.forEach((deobf, obf) -> System.out.println("  " + deobf + " -> " + obf));
        }
        Map<String, String> methods = methodMap.get(deobfClass);
        if (methods != null && !methods.isEmpty()) {
            System.out.println("Methods:");
            methods.forEach((deobf, obf) -> System.out.println("  " + deobf + " -> " + obf));
        }
        Map<String, String> nestedClasses = nestedClassMap.get(deobfClass);
        if (nestedClasses != null && !nestedClasses.isEmpty()) {
            System.out.println("Nested Classes:");
            nestedClasses.forEach((deobf, obf) ->
                    System.out.println("  " + deobf + " -> " + obf + " (full: " +
                            getObfNestedClassFull(deobfClass, deobf) + ")"));
        }
    }

    public void printAllClassMappings() {
        System.out.println("=== All Class Mappings ===");
        classMap.forEach((deobf, obf) -> System.out.println(deobf + " -> " + obf));
        System.out.println("\n=== All Nested Class Mappings ===");
        nestedClassMap.forEach((parent, nested) -> {
            System.out.println("Parent: " + parent);
            nested.forEach((deobf, obf) -> System.out.println("  " + deobf + " -> " + obf));
        });
    }
}
