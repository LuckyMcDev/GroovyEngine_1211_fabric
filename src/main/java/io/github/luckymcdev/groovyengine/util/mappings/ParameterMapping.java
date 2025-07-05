package io.github.luckymcdev.groovyengine.util.mappings;

/** One parameter’s index + names. */
public class ParameterMapping {
    private final int index;
    private final String named, obfuscated;
    public ParameterMapping(int index, String named, String obfuscated) {
        this.index = index;
        this.named = named;
        this.obfuscated = obfuscated;
    }
    public int getIndex()       { return index; }
    public String getNamed()    { return named; }
    public String getObfuscated(){ return obfuscated; }
}
