package io.github.luckymcdev.api.scripting.exposed;


import io.github.luckymcdev.GroovyEngine;

public class GroovyLogger {

    private final String scriptName;

    public GroovyLogger(String scriptFileName) {
        this.scriptName = scriptFileName;
    }

    public void info(String message) {
        GroovyEngine.LOGGER.info("[{}] {}", scriptName, message);
    }

    public void warn(String message) {
        GroovyEngine.LOGGER.warn("[{}] {}", scriptName, message);
    }

    public void error(String message) {
        GroovyEngine.LOGGER.error("[{}] {}", scriptName, message);
    }
}
