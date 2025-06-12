package io.github.luckymcdev.api.editor;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MainEditorScreen extends Screen {

    public MainEditorScreen(Text title) {
        super(title);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {

        MainEditorImGui.render();

    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
