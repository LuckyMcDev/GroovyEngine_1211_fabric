package io.github.luckymcdev.groovyengine.editor.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class MainEditorScreen extends Screen {

    public MainEditorScreen(Component title) {
        super(title);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {

        MainEditorImGui.render();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
