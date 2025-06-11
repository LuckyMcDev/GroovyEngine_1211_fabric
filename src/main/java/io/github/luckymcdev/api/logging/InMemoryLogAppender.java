package io.github.luckymcdev.api.logging;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Plugin(name = "InMemoryLogAppender", category = "Core", elementType = "appender", printObject = true)
public class InMemoryLogAppender extends AbstractAppender {

    private static final List<String> logLines = new LinkedList<>();
    private static final int MAX_LINES = 1000;

    protected InMemoryLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        synchronized (logLines) {
            String message = event.getLevel() + " | " + event.getLoggerName() + " | " + event.getMessage().getFormattedMessage();
            logLines.add(message);
            if (logLines.size() > MAX_LINES) {
                logLines.remove(0);
            }
        }
    }

    public static List<String> getLogLines() {
        synchronized (logLines) {
            return new LinkedList<>(logLines);
        }
    }

    @PluginFactory
    public static Appender createAppender(
            @PluginAttribute("name") String name
    ) {
        return new InMemoryLogAppender(name, null, null, true);
    }
}

