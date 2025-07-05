package io.github.luckymcdev.groovyengine.scripting.core;

import io.github.luckymcdev.groovyengine.util.mappings.MappingsParser;
import io.github.luckymcdev.groovyengine.util.mappings.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {
    private final MappingsParser mappings;

    /** simplename → intermediary simplename, e.g. RotationAxis → class_7833 */
    private final Map<String,String> classMap = new LinkedHashMap<>();
    private final Map<String,Map<String,String>> fieldMap = new HashMap<>();
    private final Map<String,Map<String,String>> methodMap = new HashMap<>();

    // matches: import   net.minecraft.xxx.Yyy    ;?   (trims semicolon if present)
    private static final Pattern IMPORT = Pattern.compile("^\\s*import\\s+([\\w\\.]+)(?:;\\s*)?$");

    public ScriptParser(MappingsParser mappings) {
        this.mappings = mappings;
    }

    public String remapScript(String src) {
        StringBuilder out = new StringBuilder();
        String[] lines = src.split("\n", -1);
        Set<String> importedMinecraftClasses = new HashSet<>();

        // 1) Rewrite imports to alias the obf class
        for (String line : lines) {
            Matcher m = IMPORT.matcher(line);
            if (m.find() && m.group(1).startsWith("net.minecraft")) {
                String originalFqn = m.group(1);
                String slash = originalFqn.replace('.', '/');
                MappingsClass mc = mappings.getClassMapping(slash);
                if (mc != null) {
                    String iSlashFqn = mc.getIntermediary();
                    String iDotFqn = iSlashFqn.replace('/', '.');
                    String simple = simpleName(originalFqn);
                    classMap.put(simple, simpleName(iDotFqn));

                    out.append("import ")
                            .append(iDotFqn)
                            .append(" as ")
                            .append(simple)
                            .append("\n");

                    importedMinecraftClasses.add(simple); // Remember for later
                    continue;
                }
            }
            out.append(line).append("\n");
        }

        String code = out.toString();

        // 2) Inject inner-class builder method aliases (like Item.Settings)
        Map<String, String> innerAliases = new LinkedHashMap<>();

        for (String simpleName : importedMinecraftClasses) {
            MappingsClass mc = mappings.getClassMapping("net/minecraft/" + simpleName);
            if (mc == null) continue;

            String intermediaryOuter = simpleName(mc.getIntermediary()).replace('/', '.');

            for (MethodMapping mm : mc.getMethods()) {
                String sig = mm.getSignature(); // e.g. ()Lnet/minecraft/item/Item$Settings;
                int dollar = sig.indexOf('$');
                int semi = sig.indexOf(';', dollar);
                if (dollar > 0 && semi > dollar) {
                    String inner = sig.substring(dollar + 1, semi);
                    String alias = "net.minecraft." + intermediaryOuter + "$" + mm.getObfuscated();
                    innerAliases.put(inner, alias);
                }
            }
        }

        if (!innerAliases.isEmpty()) {
            StringBuilder aliasLines = new StringBuilder();
            for (var e : innerAliases.entrySet()) {
                aliasLines.append("import ")
                        .append(e.getValue())
                        .append(" as ")
                        .append(e.getKey())
                        .append("\n");
            }
            code = aliasLines.toString() + code;
        }

        // 3) Class usages
        for (var e : classMap.entrySet()) {
            code = wordReplace(code, e.getKey(), e.getValue());
        }

        // 4) Static field accesses
        for (var ce : fieldMap.entrySet()) {
            String simple = ce.getKey();
            String iSimple = classMap.get(simple);
            for (var fe : ce.getValue().entrySet()) {
                code = code.replaceAll(
                        "\\b" + Pattern.quote(iSimple) + "\\." + Pattern.quote(fe.getKey()) + "\\b",
                        iSimple + "." + fe.getValue()
                );
            }
        }

        // 5) Static & instance methods
        for (var ce : methodMap.entrySet()) {
            String simple = ce.getKey();
            String iSimple = classMap.get(simple);
            for (var me : ce.getValue().entrySet()) {
                code = code.replaceAll(
                        "\\b" + Pattern.quote(iSimple) + "\\." + Pattern.quote(me.getKey()) + "\\b",
                        iSimple + "." + me.getValue()
                );
                code = code.replaceAll(
                        "\\." + Pattern.quote(me.getKey()) + "\\s*\\(",
                        "." + me.getValue() + "("
                );
            }
        }

        return code;
    }


    private static String simpleName(String fqn) {
        int i = fqn.lastIndexOf('.');
        return (i < 0) ? fqn : fqn.substring(i + 1);
    }

    private static String wordReplace(String text, String word, String repl) {
        return text.replaceAll("\\b" + Pattern.quote(word) + "\\b", repl);
    }
}
