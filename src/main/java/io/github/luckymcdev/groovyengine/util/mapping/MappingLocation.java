package io.github.luckymcdev.groovyengine.util.mapping;

import java.io.InputStream;

public class MappingLocation {
    private final String resourcePath; // e.g., "/assets/groovyengine/mappings/net/minecraft/item/Item.mapping"

    private MappingLocation(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public static MappingLocation of(String packagePath, String className) {
        String pkgPath = packagePath.replace('.', '/');
        String resourcePath = "/assets/groovyengine/mappings/" + pkgPath + "/" + className + ".mapping";
        return new MappingLocation(resourcePath);
    }

    public InputStream openStream() {
        return MappingLocation.class.getResourceAsStream(resourcePath);
    }

    @Override
    public String toString() {
        return resourcePath;
    }
}
