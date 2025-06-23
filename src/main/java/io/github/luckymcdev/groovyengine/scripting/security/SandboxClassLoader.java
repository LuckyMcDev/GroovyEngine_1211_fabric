package io.github.luckymcdev.groovyengine.scripting.security; // New package for security classes

import io.github.luckymcdev.groovyengine.GroovyEngine;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.security.ProtectionDomain;
import java.security.CodeSource;
import java.security.Permissions;
import java.util.Set;
import java.util.Collections;

public class SandboxClassLoader extends URLClassLoader {

    private static final Set<String> FORBIDDEN_CLASSES = Set.of(
            "java.io.File",
            "java.io.FileWriter",
            "java.io.FileReader",
            "java.io.FileOutputStream",
            "java.io.FileInputStream",
            "java.io.RandomAccessFile",
            "java.net.Socket",
            "java.net.ServerSocket",
            "java.net.URL",
            "java.net.URLConnection",
            "java.net.HttpURLConnection",
            "java.net.DatagramSocket",
            "java.net.MulticastSocket",
            "java.lang.System",
            "java.lang.Runtime",
            "java.lang.ProcessBuilder",
            "java.lang.Thread",
            "java.lang.ThreadGroup",
            "java.lang.ClassLoader",
            "java.security.AccessController",
            "java.security.Policy",
            "java.security.Permissions",
            "java.security.ProtectionDomain",
            "java.lang.reflect.Method",
            "java.lang.reflect.Field",
            "java.lang.reflect.Constructor",
            "java.lang.reflect.AccessibleObject",
            "sun.misc.Unsafe",
            "jdk.internal.misc.Unsafe",
            "javax.script.ScriptEngineManager",
            "groovy.lang.GroovyShell",
            "groovy.lang.GroovyClassLoader"
    );

    private static final Set<String> FORBIDDEN_PACKAGE_PREFIXES = Set.of(
            "java.io.",
            "java.net.",
            "java.security.",
            "java.nio.",
            "java.awt.",
            "javax.",
            "sun.", "com.sun.", "jdk.internal.",
            "org.objectweb.asm.",
            "org.spongepowered.",
            "org.apache.logging.log4j.",
            "net.fabricmc.api.",
            "net.fabricmc.loader.",
            "net.minecraft.server.network.",
            "net.minecraft.client.network."
    );

    private static final Set<String> ALLOWED_PACKAGE_PREFIXES = Set.of(
            "java.lang.",
            "java.util.",
            "java.math.",
            "groovy.lang.",
            "groovy.transform.stc.",
            "net.minecraft.block.",
            "net.minecraft.item.",
            "net.minecraft.util.",
            "net.minecraft.entity.",
            "net.minecraft.text.",
            "net.minecraft.sound.",
            "net.minecraft.particle.",
            "net.minecraft.registry.",
            "com.mojang.brigadier.",
            "org.joml.",
            "imgui.",
            "io.github.luckymcdev.groovyengine.scripting.builders.",
            "io.github.luckymcdev.groovyengine.scripting.eventservice.events.",
            "io.github.luckymcdev.groovyengine.scripting.gui.",
            "io.github.luckymcdev.groovyengine.scripting.input.",
            "io.github.luckymcdev.groovyengine.scripting.utils.",
            "io.github.luckymcdev.groovyengine.scripting.core.GroovyEngineInitializer",
            "io.github.luckymcdev.groovyengine.util."
    );


    public SandboxClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }
        if (FORBIDDEN_CLASSES.contains(name)) {
            GroovyEngine.LOGGER.warn("Sandbox: Denying load of forbidden class: {}", name);
            throw new SecurityException("Access to forbidden class: " + name + " is denied by sandbox.");
        }

        for (String prefix : FORBIDDEN_PACKAGE_PREFIXES) {
            if (name.startsWith(prefix)) {
                boolean isExplicitlyAllowed = ALLOWED_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith);
                if (!isExplicitlyAllowed) {
                    GroovyEngine.LOGGER.warn("Sandbox: Denying load of class from forbidden package: {}", name);
                    throw new SecurityException("Access to class in forbidden package: " + name + " is denied by sandbox.");
                }
            }
        }


        try {
            Class<?> c = findClass(name);
            if (resolve) {
                resolveClass(c);
            }
            return c;
        } catch (ClassNotFoundException ignored) {

        }


        try {
            boolean isAllowedPrefix = ALLOWED_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith);

            if (isAllowedPrefix ||
                    !(FORBIDDEN_CLASSES.contains(name) ||
                            FORBIDDEN_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith))
            ) {
                return super.loadClass(name, resolve);
            } else {

                GroovyEngine.LOGGER.warn("Sandbox: Denying load of class: {} (not explicitly allowed and not found in script sources)", name);
                throw new SecurityException("Class: " + name + " cannot be loaded by sandbox. (Default Deny)");
            }

        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Class " + name + " not found by any classloader in sandbox.", e);
        }
    }

    @Override
    protected java.security.PermissionCollection getPermissions(CodeSource codesource) {

        return new Permissions();
    }
}