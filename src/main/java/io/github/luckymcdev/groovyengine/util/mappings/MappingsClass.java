package io.github.luckymcdev.groovyengine.util.mappings;

import java.util.ArrayList;
import java.util.List;

/** Holds everything known about one class in your mapping JSON. */
public class MappingsClass {
    private final String named, intermediary, obfuscated;
    private final List<FieldMapping> fields = new ArrayList<>();
    private final List<MethodMapping> methods = new ArrayList<>();

    public MappingsClass(String named, String intermediary, String obfuscated) {
        this.named = named;
        this.intermediary = intermediary;
        this.obfuscated = obfuscated;
    }

    public String getNamed()        { return named; }
    public String getIntermediary(){ return intermediary; }
    public String getObfuscated()   { return obfuscated; }

    public List<FieldMapping> getFields()   { return fields; }
    public List<MethodMapping> getMethods() { return methods; }
}