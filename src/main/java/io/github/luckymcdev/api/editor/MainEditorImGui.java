package io.github.luckymcdev.api.editor;

import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import imgui.ImGui;
import io.github.luckymcdev.api.logging.InMemoryLogAppender;
import net.minecraft.client.MinecraftClient;

public class MainEditorImGui {

    public static void render() {

        if (EditorState.open) {
            MinecraftClient.getInstance().mouse.unlockCursor();

        } else {
            MinecraftClient.getInstance().mouse.lockCursor();
        }

        ImGuiImpl.draw(io -> {

            ImGui.begin("Console");

            for (String line : InMemoryLogAppender.getLogLines()) {
                // Optional: color based on log level
                if (line.startsWith("ERROR")) ImGui.textColored(1, 0, 0, 1, line);
                else if (line.startsWith("WARN")) ImGui.textColored(1, 1, 0, 1, line);
                else ImGui.textWrapped(line);
            }

            if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
                ImGui.setScrollHereY(1.0f);
            }

            ImGui.end();


            ImGui.begin("Script Browser");
            ImGui.end();

            ImGui.begin("Blockdisplay Browser");
            ImGui.end();
        });
    }
}
