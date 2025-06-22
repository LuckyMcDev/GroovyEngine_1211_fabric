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

    // Package prefixes that are explicitly forbidden. Loading any class from these packages will fail.
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
            "java.lang.", // Core Java types (String, Integer, etc.)
            "java.util.", // Collections, UUID, Duration, etc.
            "java.math.", // BigInteger, BigDecimal
            "groovy.lang.", // Core Groovy runtime types
            "groovy.transform.stc.", // For Static Type Checking (if you need it)
            "net.minecraft.block.",
            "net.minecraft.item.",
            "net.minecraft.util.",
            "net.minecraft.entity.",
            "net.minecraft.text.",
            "net.minecraft.sound.",
            "net.minecraft.particle.",
            "net.minecraft.registry.",
            "com.mojang.brigadier.",
            "org.joml.", // JOML types
            "imgui.", // ImGui classes (if you expose them)
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
        // 1. Check if the class is already loaded by this classloader
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 2. Security Checks (Order matters here!)
        // First, explicitly forbidden classes
        if (FORBIDDEN_CLASSES.contains(name)) {
            GroovyEngine.LOGGER.warn("Sandbox: Denying load of forbidden class: {}", name);
            throw new SecurityException("Access to forbidden class: " + name + " is denied by sandbox.");
        }

        // Then, forbidden packages
        for (String prefix : FORBIDDEN_PACKAGE_PREFIXES) {
            if (name.startsWith(prefix)) {
                // If it's a forbidden package, ensure it's not also in an allowed prefix.
                // This prevents cases where a safe class could be within a forbidden package
                // (less common but good for robustness).
                boolean isExplicitlyAllowed = ALLOWED_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith);
                if (!isExplicitlyAllowed) {
                    GroovyEngine.LOGGER.warn("Sandbox: Denying load of class from forbidden package: {}", name);
                    throw new SecurityException("Access to class in forbidden package: " + name + " is denied by sandbox.");
                }
            }
        }

        // 3. Try to load from this classloader's URLs (i.e., the script classes themselves)
        try {
            // findClass will only look in the URLs provided to this classloader
            Class<?> c = findClass(name);
            if (resolve) {
                resolveClass(c);
            }
            return c;
        } catch (ClassNotFoundException e) {
            // Not found in our URLs, proceed to parent
        }


        // 4. Delegate to parent for system classes or explicitly allowed classes
        // This is crucial: parent classloader should load standard library classes,
        // unless they are explicitly forbidden by our rules above.
        try {
            // Check if the class being requested is one of our explicitly allowed prefixes
            boolean isAllowedPrefix = ALLOWED_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith);

            // If it's an allowed prefix OR not caught by forbidden rules,
            // try loading it via the parent classloader.
            // This allows the script to use safe standard Java/Minecraft APIs.
            if (isAllowedPrefix ||
                    !(FORBIDDEN_CLASSES.contains(name) ||
                            FORBIDDEN_PACKAGE_PREFIXES.stream().anyMatch(name::startsWith))
            ) {
                return super.loadClass(name, resolve);
            } else {
                // This branch should ideally not be reached if the forbidden checks above are comprehensive,
                // but acts as a final safeguard.
                GroovyEngine.LOGGER.warn("Sandbox: Denying load of class: {} (not explicitly allowed and not found in script sources)", name);
                throw new SecurityException("Class: " + name + " cannot be loaded by sandbox. (Default Deny)");
            }

        } catch (ClassNotFoundException e) {
            // Class not found by parent either.
            throw new ClassNotFoundException("Class " + name + " not found by any classloader in sandbox.", e);
        }
    }

    /**
     * Override getPermissions if you plan to use a Java Security Manager along with this ClassLoader.
     * This method defines what permissions code loaded by this ClassLoader will have.
     * For now, we'll keep it simple (granting no special permissions), as the ClassLoader itself is the main guard.
     */
    @Override
    protected java.security.PermissionCollection getPermissions(CodeSource codesource) {
        // By default, grant no specific permissions beyond what is inherited
        // if a SecurityManager is active. The ClassLoader's primary role
        // is to prevent class loading, not to grant/deny runtime permissions.
        return new Permissions(); // An empty Permissions object effectively denies all
    }
}