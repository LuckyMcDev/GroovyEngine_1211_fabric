package io.github.luckymcdev.groovyengine.editor.windows;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTabItemFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import io.github.luckymcdev.groovyengine.GroovyEngine;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class EditorWindow {

    // --- File System Configuration ---
    private static final Path SCRIPTS_ROOT_PATH = FabricLoader.getInstance().getGameDir().resolve("GroovyEngine/scripts");

    // --- Sidebar State ---
    // Change to Path to be consistent with deeper file traversal and easier relative path calculation
    private static final List<Path> scriptPathsInSidebar = new ArrayList<>(); // Paths found in SCRIPTS_ROOT_PATH and subdirectories
    private static Path selectedSidebarPath = null; // Path of the file currently selected in the sidebar

    // --- Editor Tabs State ---
    private static final List<EditorTab> openTabs = new ArrayList<>();
    private static EditorTab activeTab = null; // The currently active tab
    private static int newFileCounter = 1; // Counter for naming new files

    // --- Popup State for Save Confirmation ---
    private static EditorTab tabToConfirmClose = null;

    private static class EditorTab {
        public Path filePath;
        public String tabId;
        public String displayName;
        public ImString content;
        public boolean dirty;
        public boolean existsOnDisk;

        public EditorTab(Path filePath, String displayName, String initialContent, boolean existsOnDisk) {
            this.filePath = filePath;
            this.tabId = "##" + (filePath != null ? filePath.toAbsolutePath().hashCode() : UUID.randomUUID().toString());
            this.displayName = displayName;
            this.content = new ImString(initialContent, 65536);
            this.dirty = false;
            this.existsOnDisk = existsOnDisk;
        }
    }

    public static void draw() {
        ImGui.begin("GroovyEngine Script Editor", ImGuiWindowFlags.MenuBar);

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("New Script")) {
                    createNewScriptTab();
                }
                if (ImGui.menuItem("Save Active")) {
                    if (activeTab != null && activeTab.dirty) {
                        saveActiveTab();
                    }
                }
                if (ImGui.menuItem("Save All")) {
                    saveAllDirtyTabs();
                }
                ImGui.separator();
                if (ImGui.menuItem("Refresh Script List")) {
                    refreshScriptList(true);
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        ImGui.beginChild("ScriptList", 400, 0, true);
        refreshScriptList(false);

        if (ImGui.button("Refresh List")) {
            refreshScriptList(true);
        }
        ImGui.separator();


        for (Path scriptPath : scriptPathsInSidebar) {
            String relativeDisplayName = SCRIPTS_ROOT_PATH.relativize(scriptPath).toString().replace(File.separatorChar, '/');

            boolean isSelectedInSidebar = Objects.equals(scriptPath, selectedSidebarPath);

            if (ImGui.selectable(relativeDisplayName, isSelectedInSidebar)) {
                selectedSidebarPath = scriptPath;
                openScript(scriptPath);
            }
        }
        ImGui.endChild();

        ImGui.sameLine();

        ImGui.beginChild("EditorArea", 0, 0, false, ImGuiWindowFlags.NoScrollbar); // No scrollbar, tabs manage it

        if (openTabs.isEmpty()) {
            ImGui.text("Open a script from the left or create a new one.");
        } else {
            if (ImGui.beginTabBar("##EditorTabs", ImGuiTabItemFlags.None)) {
                EditorTab nextActiveTab = null;


                Iterator<EditorTab> iterator = openTabs.iterator();
                while (iterator.hasNext()) {
                    EditorTab tab = iterator.next();

                    String tabTitle = tab.displayName + (tab.dirty ? "*" : "");
                    ImBoolean pOpen = new ImBoolean(true);

                    int tabFlags = 0;
                    if (tab == activeTab) {
                        tabFlags |= ImGuiTabItemFlags.SetSelected;
                    }

                    if (ImGui.beginTabItem(tabTitle, pOpen, tabFlags | ImGuiTabItemFlags.None)) {
                        activeTab = tab;
                        nextActiveTab = tab;

                        if (ImGui.inputTextMultiline("##editor_" + tab.tabId, tab.content,
                                ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(),
                                ImGuiInputTextFlags.AllowTabInput)) {
                            tab.dirty = true;
                        }

                        ImGui.endTabItem();
                    }

                    if (!pOpen.get()) {
                        if (tab.dirty) {
                            tabToConfirmClose = tab;
                            ImGui.openPopup("SaveConfirmationPopup");
                        } else {
                            GroovyEngine.LOGGER.info("Closing clean tab: {}", tab.displayName);
                            iterator.remove();
                            tab.content.clear();
                            if (tab == activeTab) {
                                activeTab = null;
                            }
                        }
                    }
                }


                if (nextActiveTab == null && !openTabs.isEmpty()) {
                    activeTab = openTabs.get(0);
                } else if (openTabs.isEmpty()) {
                    activeTab = null;
                } else {
                    activeTab = nextActiveTab;
                }

                ImGui.endTabBar();
            }
        }
        ImGui.endChild();
        ImGui.end();

        renderSaveConfirmationPopup();
        renderSaveAsPopup();
    }

    private static void createNewScriptTab() {
        String newFileName = "Untitled-" + newFileCounter + ".groovy";
        Path newFilePath = SCRIPTS_ROOT_PATH.resolve(newFileName);


        Path finalNewFilePath = newFilePath;
        while (Files.exists(newFilePath) || openTabs.stream().anyMatch(tab -> Objects.equals(tab.filePath, finalNewFilePath))) {
            newFileCounter++;
            newFileName = "Untitled-" + newFileCounter + ".groovy";
            newFilePath = SCRIPTS_ROOT_PATH.resolve(newFileName);
        }
        newFileCounter++;

        EditorTab newTab = new EditorTab(null, newFileName, "// New Groovy Script\n", false);
        openTabs.add(newTab);
        activeTab = newTab;
        GroovyEngine.LOGGER.info("Created new script tab: {}", newFileName);
    }

    private static void saveActiveTab() {
        if (activeTab == null) return;

        if (activeTab.filePath == null || !activeTab.existsOnDisk) {

            promptSaveAs(activeTab);
        } else {

            try {
                Files.writeString(activeTab.filePath, activeTab.content.get());
                activeTab.dirty = false;
                GroovyEngine.LOGGER.info("Saved script: {}", activeTab.filePath.getFileName());

                if (!scriptPathsInSidebar.contains(activeTab.filePath)) {
                    refreshScriptList(true);
                }
            } catch (IOException e) {
                GroovyEngine.LOGGER.error("Failed to save script {}: {}", activeTab.filePath.getFileName(), e.getMessage());
                // TODO: Show an ImGui error popup
            }
        }
    }

    private static void saveAllDirtyTabs() {
        for (EditorTab tab : openTabs) {
            if (tab.dirty) {
                if (tab.filePath != null && tab.existsOnDisk) {
                    try {
                        Files.writeString(tab.filePath, tab.content.get());
                        tab.dirty = false;
                        GroovyEngine.LOGGER.info("Saved existing script from Save All: {}", tab.filePath.getFileName());
                    } catch (IOException e) {
                        GroovyEngine.LOGGER.error("Failed to save existing script {}: {}", tab.filePath.getFileName(), e.getMessage());
                    }
                } else {
                    GroovyEngine.LOGGER.warn("Cannot save new/unsaved tab '{}' via Save All. Use 'Save Active' and 'Save As' instead.", tab.displayName);
                }
            }
        }
    }

    private static ImString newFileNameInput = new ImString(256);
    private static EditorTab tabForSaveAs = null;

    private static void promptSaveAs(EditorTab tab) {
        tabForSaveAs = tab;
        ImGui.openPopup("SaveAsPopup");
        newFileNameInput.set(tab.existsOnDisk ? tab.displayName : "my_script.groovy");
    }

    private static void renderSaveAsPopup() {
        if (tabForSaveAs == null) return;

        ImGui.setNextWindowPos(ImGui.getMainViewport().getCenter().x, ImGui.getMainViewport().getCenter().y, ImGuiCond.Appearing, 0.5f, 0.5f);
        if (ImGui.beginPopupModal("SaveAsPopup", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("Save As:");
            ImGui.inputText("##newfilename", newFileNameInput);
            ImGui.text("Location: " + SCRIPTS_ROOT_PATH.toAbsolutePath().normalize().toString());
            ImGui.separator();

            if (ImGui.button("Save")) {
                String proposedName = newFileNameInput.get();
                if (!proposedName.toLowerCase().endsWith(".groovy")) {
                    proposedName += ".groovy";
                }

                Path newFilePath = SCRIPTS_ROOT_PATH.resolve(proposedName);

                if (Files.exists(newFilePath)) {
                    // TODO: Ask for overwrite confirmation (e.g., another popup)
                    GroovyEngine.LOGGER.warn("File {} already exists. Overwriting.", newFilePath.getFileName());
                }

                try {
                    Files.createDirectories(newFilePath.getParent());
                    Files.writeString(newFilePath, tabForSaveAs.content.get());

                    tabForSaveAs.filePath = newFilePath;
                    tabForSaveAs.displayName = SCRIPTS_ROOT_PATH.relativize(newFilePath).toString().replace(File.separatorChar, '/'); // Update display name for nested structure
                    tabForSaveAs.existsOnDisk = true;
                    tabForSaveAs.dirty = false;
                    GroovyEngine.LOGGER.info("Saved script as: {}", newFilePath.getFileName());
                    ImGui.closeCurrentPopup();
                    tabForSaveAs = null;
                    refreshScriptList(true);
                } catch (IOException e) {
                    GroovyEngine.LOGGER.error("Failed to save script as {}: {}", newFilePath.getFileName(), e.getMessage());
                    // TODO: Show an ImGui error popup
                }
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
                tabForSaveAs = null;
            }
            ImGui.endPopup();
        }
    }


    private static void openScript(Path scriptPath) {
        if (!Files.exists(scriptPath) || !Files.isRegularFile(scriptPath)) {
            GroovyEngine.LOGGER.warn("Tried to open non-existent or non-file path: {}", scriptPath);
            // TODO: Show an ImGui error popup
            return;
        }

        for (EditorTab tab : openTabs) {
            if (Objects.equals(tab.filePath, scriptPath)) {
                activeTab = tab;
                return;
            }
        }

        try {
            String content = Files.readString(scriptPath);
            String relativeDisplayName = SCRIPTS_ROOT_PATH.relativize(scriptPath).toString().replace(File.separatorChar, '/');
            EditorTab newTab = new EditorTab(scriptPath, relativeDisplayName, content, true);
            openTabs.add(newTab);
            activeTab = newTab;
            GroovyEngine.LOGGER.info("Opened script: {}", scriptPath.getFileName());
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to read script {}: {}", scriptPath.getFileName(), e.getMessage());
            // TODO: Show an ImGui error popup
        }
    }

    private static void refreshScriptList(boolean force) {
        if (!force && !scriptPathsInSidebar.isEmpty()) return;

        scriptPathsInSidebar.clear();

        try {
            Files.createDirectories(SCRIPTS_ROOT_PATH);

            try (Stream<Path> walk = Files.walk(SCRIPTS_ROOT_PATH, Integer.MAX_VALUE)) {
                List<Path> foundPaths = walk
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".groovy"))
                        .toList();
                scriptPathsInSidebar.addAll(foundPaths);
                scriptPathsInSidebar.sort(Comparator.comparing(p -> SCRIPTS_ROOT_PATH.relativize(p).toString().toLowerCase()));
            }
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to list script files in directory {}: {}", SCRIPTS_ROOT_PATH, e.getMessage());
            // TODO: Show an ImGui error popup
        }
    }

    private static void renderSaveConfirmationPopup() {
        if (tabToConfirmClose == null) return; // No tab pending confirmation

        ImGui.setNextWindowPos(ImGui.getMainViewport().getCenter().x, ImGui.getMainViewport().getCenter().y, ImGuiCond.Appearing, 0.5f, 0.5f);
        if (ImGui.beginPopupModal("SaveConfirmationPopup", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("Changes to '" + tabToConfirmClose.displayName + "' have not been saved.");
            ImGui.text("Do you want to save your changes?");
            ImGui.separator();

            if (ImGui.button("Save")) {
                boolean saved = false;
                if (tabToConfirmClose.filePath == null || !tabToConfirmClose.existsOnDisk) {
                    GroovyEngine.LOGGER.warn("Cannot save new/unsaved tab '{}' directly from close confirmation. Please use 'Save Active' and 'Save As'.", tabToConfirmClose.displayName);
                } else {
                    try {
                        Files.writeString(tabToConfirmClose.filePath, tabToConfirmClose.content.get());
                        tabToConfirmClose.dirty = false;
                        saved = true;
                        GroovyEngine.LOGGER.info("Saved tab '{}' via confirmation.", tabToConfirmClose.displayName);
                    } catch (IOException e) {
                        GroovyEngine.LOGGER.error("Failed to save tab '{}' via confirmation: {}", tabToConfirmClose.displayName, e.getMessage());
                        // TODO: Show error to user
                    }
                }

                if (saved) {
                    closeConfirmedTab(tabToConfirmClose);
                    ImGui.closeCurrentPopup();
                    tabToConfirmClose = null;
                }
            }
            ImGui.sameLine();
            if (ImGui.button("Discard")) {
                GroovyEngine.LOGGER.info("Discarded changes for tab '{}'.", tabToConfirmClose.displayName);
                closeConfirmedTab(tabToConfirmClose);
                ImGui.closeCurrentPopup();
                tabToConfirmClose = null;
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                GroovyEngine.LOGGER.info("Cancelled closing tab '{}'.", tabToConfirmClose.displayName);
                ImGui.closeCurrentPopup();
                tabToConfirmClose = null;
                if (activeTab != tabToConfirmClose) {
                    activeTab = tabToConfirmClose;
                }
            }
            ImGui.endPopup();
        }
    }

    private static void closeConfirmedTab(EditorTab tab) {
        int index = openTabs.indexOf(tab);
        if (index != -1) {
            openTabs.remove(index);
            tab.content.clear();

            if (tab == activeTab) {
                if (!openTabs.isEmpty()) {
                    activeTab = openTabs.get(Math.max(0, index - 1));
                } else {
                    activeTab = null;
                }
            }
        }
    }
}