package io.github.luckymcdev.groovyengine.rendering.core;

import io.github.luckymcdev.groovyengine.rendering.instances.RenderBlockInstance;
import io.github.luckymcdev.groovyengine.rendering.instances.RenderItemInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroovyRenderList {
    public static final List<RenderBlockInstance> blocks = new CopyOnWriteArrayList<>();
    public static final List<RenderItemInstance> items = new CopyOnWriteArrayList<>();

    public static void clearAll() {
        blocks.clear();
        items.clear();
    }

    public static void addBlock(RenderBlockInstance block) {
        blocks.add(block);
    }

    public static void addItem(RenderItemInstance item) {
        items.add(item);
    }

    public static void removeBlock(RenderBlockInstance block) {
        blocks.remove(block);
    }

    public static void removeItem(RenderItemInstance item) {
        items.remove(item);
    }
}
