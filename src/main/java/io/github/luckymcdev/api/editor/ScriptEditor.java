package io.github.luckymcdev.api.editor;

import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptEditor {
    private static final TextEditor editor = new TextEditor();
    private static Path currentScriptPath;
    private static boolean scriptLoaded = false;

    static {
        editor.setLanguageDefinition(TextEditorLanguageDefinition.AngelScript()); // Substitute with Groovy if you define one
    }

    public static void openScript(Path scriptPath) {
        currentScriptPath = scriptPath;
        try {
            String code = Files.readString(scriptPath);
            editor.setText(code);
            scriptLoaded = true;
        } catch (IOException e) {
            editor.setText("// Failed to load script: " + e.getMessage());
            scriptLoaded = false;
        }
    }

    public static void render() {
        if (!scriptLoaded) return;

        ImGui.begin("Script Editor");

        editor.render("ScriptEditor");

        if (ImGui.button("Save")) {
            if (currentScriptPath != null) {
                try {
                    Files.writeString(currentScriptPath, editor.getText());
                } catch (IOException e) {
                    ImGui.textColored(1, 0.2f, 0.2f, 1, "Save failed: " + e.getMessage());
                }
            }
        }

        ImGui.end();
    }

    public static boolean isScriptLoaded() {
        return scriptLoaded;
    }
}
