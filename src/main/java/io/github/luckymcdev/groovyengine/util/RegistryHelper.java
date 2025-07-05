package io.github.luckymcdev.groovyengine.util;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistryHelper<T> {
    private final Registry<T> REGISTRY;
    private final String MOD_ID;

    public RegistryHelper(@NotNull Registry<T> registry,@NotNull String modId) {

        this.REGISTRY = Objects.requireNonNull(registry, "[ERROR] Oops! Something has gone horribly wrong: 'builders' is 'null' in the parsed parameter");
        MOD_ID = Objects.requireNonNull(modId, "[ERROR] Oops! Something has gone horribly wrong: 'modId' is 'null' in the parsed parameter");
    }


    public T register(String name,T element){
        return Registry.register(REGISTRY, Identifier.of(MOD_ID,name),element);
    }
}