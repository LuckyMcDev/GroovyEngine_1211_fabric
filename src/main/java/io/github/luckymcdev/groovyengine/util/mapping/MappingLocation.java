package io.github.luckymcdev.groovyengine.util.mapping;

import java.io.InputStream;

public class MappingLocation {
    private final String packagePath; // e.g. "com.mojang.blaze3d.platform.systems"
    private final String className;   // e.g. "RenderSystem"

    private MappingLocation(String packagePath, String className) {
        this.packagePath = packagePath;
        this.className = className;
    }

    public static MappingLocation of(String packagePath, String className) {
        return new MappingLocation(packagePath, className);
    }

    /** Path like: /assets/groovyengine/mappings/com/mojang/blaze3d/platform/systems/RenderSystem.mapping */
    public String toResourcePath() {
        String pkgPath = packagePath.replace('.', '/');
        return "/assets/groovyengine/mappings/" + pkgPath + "/" + className + ".mapping";
    }

    public InputStream openStream() {
        return MappingLocation.class.getResourceAsStream(toResourcePath());
    }

    @Override
    public String toString() {
        return toResourcePath();
    }
}
