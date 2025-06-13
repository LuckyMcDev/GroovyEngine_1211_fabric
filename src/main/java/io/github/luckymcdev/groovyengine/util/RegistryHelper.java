package io.github.luckymcdev.groovyengine.util;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegistryHelper<T> {
    private final Registry<T> REGISTRY;         // Refactored to upper case as per Java Docs best practices.
    private final String MOD_ID;

    public RegistryHelper(@NotNull Registry<T> registry,@NotNull String modId) {
        // Replaced 'IF Statements' with inline 'Objects.requireNonNull' as per Java Docs best practices.
        /*
         * NOTE: 'Objects.requireNonNull' is a helpful method to spot NullPointerExceptions effectively while developing.
         *       But in fact it's throwing a RuntimeException which could crash the application (the worst thing ever in production).
         *       However, in this context it is imperative to catch this error before allowing it to continue further on since we are accessing registries.
         *
         * NOTE: This program will build when initializing this class;
         *       'RegistryHelper<T> registryHelper=new RegistryHelper<T>(null,null);',
         *       but will only throw the 'NullPointerException' at runtime.
         * */
        this.REGISTRY = Objects.requireNonNull(registry, "[ERROR] Oops! Something has gone horribly wrong: 'registry' is 'null' in the parsed parameter");
        MOD_ID = Objects.requireNonNull(modId, "[ERROR] Oops! Something has gone horribly wrong: 'modId' is 'null' in the parsed parameter");
    }


    public T register(String name,T element){
        return Registry.register(REGISTRY, Identifier.of(MOD_ID,name),element);
    }
}