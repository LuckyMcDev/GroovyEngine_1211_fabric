package io.github.luckymcdev.groovyengine.util.mappings;

import java.util.ArrayList;
import java.util.List;

/** One method’s signature + all namespace names + its parameters. */
public class MethodMapping {
    private final String signature, named, intermediary, obfuscated;
    private final List<ParameterMapping> parameters = new ArrayList<>();

    public MethodMapping(String signature, String named, String intermediary, String obfuscated) {
        this.signature  = signature;
        this.named      = named;
        this.intermediary = intermediary;
        this.obfuscated   = obfuscated;
    }
    public String getSignature()    { return signature; }
    public String getNamed()        { return named; }
    public String getIntermediary(){ return intermediary; }
    public String getObfuscated()   { return obfuscated; }
    public List<ParameterMapping> getParameters() { return parameters; }
}
