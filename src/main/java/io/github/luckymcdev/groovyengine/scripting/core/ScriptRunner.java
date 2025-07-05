package io.github.luckymcdev.groovyengine.scripting.core;

import groovy.lang.GroovyShell;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import io.github.luckymcdev.groovyengine.util.TinyRemapper;
import net.fabricmc.loader.api.FabricLoader;
import org.codehaus.groovy.ant.Groovy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRunner {
    private static final List<String> scriptLoadErrors = new CopyOnWriteArrayList<>();
    private static TinyRemapper tinyRemapper;
    private static final boolean IS_DEV_CHECKING_ENABLED = false;

    public static void setTinyRemapper(TinyRemapper remapper) {
        tinyRemapper = remapper;
    }

    public static List<String> getScriptLoadErrors() {
        return new ArrayList<>(scriptLoadErrors);
    }

    public static void clearErrors() {
        scriptLoadErrors.clear();
    }

    private static String remapAll(String source) {
        if (tinyRemapper == null || (IS_DEV_CHECKING_ENABLED && FabricLoader.getInstance().isDevelopmentEnvironment())) {
            return source;
        }

        Map<String, String> allMappings = new LinkedHashMap<>();

        // 1. Inner classes first (dotted)
        tinyRemapper.innerClassMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey().length(), a.getKey().length()))
                .forEach(e -> allMappings.put(e.getKey(), e.getValue()));

        // 2. Top-level classes
        tinyRemapper.classMap.forEach((k, v) -> {
            String from = k.replace('/', '.');
            String to = v.replace('/', '.');
            allMappings.put(from, to);
            allMappings.put(from.substring(from.lastIndexOf('.') + 1), to); // bare reference
        });

        // 3. Fields
        tinyRemapper.fieldMap.forEach(allMappings::put);

        // 4. Methods
        tinyRemapper.methodMap.forEach(allMappings::put);

        // First pass: remap import lines (adds `as` if needed)
        Matcher importMatcher = Pattern.compile("import\\s+([\\w\\.]+)").matcher(source);
        StringBuffer importBuffer = new StringBuffer();
        while (importMatcher.find()) {
            String fullClass = importMatcher.group(1);
            String remapped = allMappings.get(fullClass);
            if (remapped != null) {
                String simpleName = remapped.substring(remapped.lastIndexOf('.') + 1);
                importMatcher.appendReplacement(importBuffer, "import " + remapped + " as " + simpleName);
            } else {
                importMatcher.appendReplacement(importBuffer, importMatcher.group(0));
            }
        }
        importMatcher.appendTail(importBuffer);
        source = importBuffer.toString();

        // Second pass: remap all references using \b boundaries
        List<Pattern> patterns = new ArrayList<>();
        List<String> replacements = new ArrayList<>();

        for (Map.Entry<String, String> entry : allMappings.entrySet()) {
            patterns.add(Pattern.compile("\\b" + Pattern.quote(entry.getKey()) + "\\b"));
            replacements.add(entry.getValue());
        }

        for (int i = 0; i < patterns.size(); i++) {
            Matcher matcher = patterns.get(i).matcher(source);
            source = matcher.replaceAll(replacements.get(i));
        }

        return source;
    }


    public static void runScriptsInFolder(Path folder) {
        clearErrors();
        if (!Files.exists(folder)) {
            GroovyEngine.LOGGER.warn("[GroovyEngine] folder not found: {}", folder);
            return;
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*.groovy")) {
            List<Path> scripts = new ArrayList<>();
            ds.forEach(scripts::add);
            scripts.sort(Comparator.comparingInt(ScriptMetadata::getPriority));

            for (Path script : scripts) {
                if (ScriptMetadata.isDisabled(script)) continue;

                GroovyEngine.LOGGER.info("Loading {}", script.getFileName());
                GroovyShell shell = ScriptShellFactory.createShell(script);

                try {
                    String raw = Files.readString(script, StandardCharsets.UTF_8);
                    String remapped = remapAll(raw);

                    GroovyEngine.LOGGER.error(remapped);

                    shell.evaluate(remapped, script.getFileName().toString());

                    GroovyEngine.LOGGER.info("Loaded {}", script.getFileName());
                } catch (Exception ex) {
                    String err = "Error in " + script.getFileName() + " – " + ex.getMessage();
                    GroovyEngine.LOGGER.error(err, ex);
                    scriptLoadErrors.add(err);
                }
            }
        } catch (IOException e) {
            String err = "Failed reading scripts: " + e.getMessage();
            GroovyEngine.LOGGER.error(err, e);
            scriptLoadErrors.add(err);
        }
    }
}
