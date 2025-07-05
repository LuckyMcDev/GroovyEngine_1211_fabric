package io.github.luckymcdev.groovyengine.util.mappings;

/** One field’s descriptor + all namespace names. */
public class FieldMapping {
    private final String descriptor, named, intermediary, obfuscated;
    public FieldMapping(String descriptor, String named, String intermediary, String obfuscated) {
        this.descriptor = descriptor;
        this.named      = named;
        this.intermediary = intermediary;
        this.obfuscated   = obfuscated;
    }
    public String getDescriptor()   { return descriptor; }
    public String getNamed()        { return named; }
    public String getIntermediary(){ return intermediary; }
    public String getObfuscated()   { return obfuscated; }
}
