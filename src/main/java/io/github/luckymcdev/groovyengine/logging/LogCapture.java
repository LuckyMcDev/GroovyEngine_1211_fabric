package io.github.luckymcdev.groovyengine.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class LogCapture {

    public static void hookLog4j() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();

        InMemoryLogAppender appender = (InMemoryLogAppender) InMemoryLogAppender.createAppender("InMemoryAppender");
        appender.start();

        rootLogger.addAppender(appender);
    }
}

