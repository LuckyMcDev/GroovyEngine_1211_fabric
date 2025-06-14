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
import java.util.stream.Collectors;
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
    private static EditorTab tabToConfirmClose = null; // Tab that needs save confirmation before closing

    // --- Inner Class to Represent an Open Editor Tab ---
    private static class EditorTab {
        public Path filePath; // Change from File to Path for consistency; null for new, unsaved files
        public String tabId; // Unique ID for ImGui tab
        public String displayName; // Name displayed on the tab (e.g., "my_script.groovy" or "client/testing.groovy")
        public ImString content;
        public boolean dirty;
        public boolean existsOnDisk; // True if it's a file loaded from disk, false if new

        public EditorTab(Path filePath, String displayName, String initialContent, boolean existsOnDisk) {
            this.filePath = filePath;
            // Use filePath's absolute path hash for tabId for consistency, or UUID for truly new files
            this.tabId = "##" + (filePath != null ? filePath.toAbsolutePath().hashCode() : UUID.randomUUID().toString());
            this.displayName = displayName;
            this.content = new ImString(initialContent, 65536); // 64KB buffer
            this.dirty = false;
            this.existsOnDisk = existsOnDisk;
        }
    }

    public static void draw() {
        // Main window with a menu bar
        ImGui.begin("GroovyEngine Script Editor", ImGuiWindowFlags.MenuBar);

        // --- EditorWindow Menu Bar (File Operations) ---
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
                    refreshScriptList(true); // Force refresh
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        // --- Layout: Sidebar and Editor Area ---
        ImGui.beginChild("ScriptList", 400, 0, true);
        // We now always refresh the script list at start to ensure all subdirs are found
        // but you can optimize this based on your needs (e.g., only on startup, or with a timer)
        refreshScriptList(false); // Default to not force, but will refresh if empty

        if (ImGui.button("Refresh List")) { // Explicit refresh button for sidebar
            refreshScriptList(true);
        }
        ImGui.separator();

        // Display files, handling subdirectories for display
        for (Path scriptPath : scriptPathsInSidebar) {
            // Get the path relative to SCRIPTS_ROOT_PATH for display in the sidebar
            String relativeDisplayName = SCRIPTS_ROOT_PATH.relativize(scriptPath).toString().replace(File.separatorChar, '/');

            // Determine if this file is currently selected in the sidebar
            boolean isSelectedInSidebar = Objects.equals(scriptPath, selectedSidebarPath);

            if (ImGui.selectable(relativeDisplayName, isSelectedInSidebar)) {
                selectedSidebarPath = scriptPath;
                openScript(scriptPath); // Request to open the script as a new tab
            }
        }
        ImGui.endChild();

        ImGui.sameLine();

        // --- Editor Tabs Area ---
        ImGui.beginChild("EditorArea", 0, 0, false, ImGuiWindowFlags.NoScrollbar); // No scrollbar, tabs manage it

        if (openTabs.isEmpty()) {
            ImGui.text("Open a script from the left or create a new one.");
        } else {
            // Tab Bar
            // FIX: Remove ImGuiTabItemFlags.NoCloseWithMiddleMouseButton
            //      or use ImGuiTabItemFlags.None if you don't need reordering
            //      ImGuiTabItemFlags.Reorderable is okay if you want to drag tabs
            if (ImGui.beginTabBar("##EditorTabs", ImGuiTabItemFlags.None)) {
                EditorTab nextActiveTab = null; // To store which tab becomes active after current frame

                // Iterate through tabs to draw them
                Iterator<EditorTab> iterator = openTabs.iterator();
                while (iterator.hasNext()) {
                    EditorTab tab = iterator.next();
                    // Display name with dirty indicator
                    String tabTitle = tab.displayName + (tab.dirty ? "*" : "");
                    ImBoolean pOpen = new ImBoolean(true); // For the close button

                    int tabFlags = 0;
                    if (tab == activeTab) {
                        tabFlags |= ImGuiTabItemFlags.SetSelected;
                    }

                    // --- CRITICAL CHANGE HERE ---
                    // If you want a close button 'x', you need ImGuiTabItemFlags.Closable
                    // But for simple switching, the ImGui.beginTabItem(label) itself handles selection.
                    // If you want a close button, it should be:
                    // ImGui.beginTabItem(tabTitle, pOpen, tabFlags | ImGuiTabItemFlags.Closable)
                    // If you don't want an explicit 'x' button, just omit pOpen and Closable flag.
                    // For now, let's assume you want the 'x' button.

                    if (ImGui.beginTabItem(tabTitle, pOpen, tabFlags | ImGuiTabItemFlags.None)) {
                        // This tab is now active
                        activeTab = tab;
                        nextActiveTab = tab; // Keep track for the end of the loop

                        // InputTextMultiline for the actual editor content
                        if (ImGui.inputTextMultiline("##editor_" + tab.tabId, tab.content,
                                ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY(),
                                ImGuiInputTextFlags.AllowTabInput)) {
                            tab.dirty = true;
                        }

                        ImGui.endTabItem();
                    }

                    // Handle tab closing (the 'X' button)
                    if (!pOpen.get()) { // If the 'X' was clicked for this tab
                        if (tab.dirty) {
                            tabToConfirmClose = tab; // Set this tab for confirmation
                            ImGui.openPopup("SaveConfirmationPopup"); // Open the popup
                        } else {
                            // No changes, close immediately
                            GroovyEngine.LOGGER.info("Closing clean tab: {}", tab.displayName);
                            iterator.remove(); // Remove tab from list
                            tab.content.clear(); // Free ImString buffer
                            if (tab == activeTab) {
                                activeTab = null; // Clear active if it was the one closed
                            }
                        }
                    }
                }

                // If no tab was selected during the loop, set active tab to null or the first one if available
                if (nextActiveTab == null && !openTabs.isEmpty()) {
                    activeTab = openTabs.get(0); // Select the first tab if none active
                } else if (openTabs.isEmpty()) {
                    activeTab = null; // No tabs open
                } else {
                    activeTab = nextActiveTab; // Set the active tab based on user selection
                }

                ImGui.endTabBar();
            }
        }
        ImGui.endChild(); // End EditorArea

        ImGui.end(); // End main editor window

        renderSaveConfirmationPopup();
        renderSaveAsPopup(); // Render save as popup
    }

    private static void createNewScriptTab() {
        String newFileName = "Untitled-" + newFileCounter + ".groovy";
        Path newFilePath = SCRIPTS_ROOT_PATH.resolve(newFileName);

        // Ensure unique name for new untitled files if one exists
        Path finalNewFilePath = newFilePath;
        while (Files.exists(newFilePath) || openTabs.stream().anyMatch(tab -> Objects.equals(tab.filePath, finalNewFilePath))) {
            newFileCounter++;
            newFileName = "Untitled-" + newFileCounter + ".groovy";
            newFilePath = SCRIPTS_ROOT_PATH.resolve(newFileName);
        }
        newFileCounter++; // Increment for the next new file

        EditorTab newTab = new EditorTab(null, newFileName, "// New Groovy Script\n", false);
        openTabs.add(newTab);
        activeTab = newTab; // Make the new tab active
        GroovyEngine.LOGGER.info("Created new script tab: {}", newFileName);
    }

    private static void saveActiveTab() {
        if (activeTab == null) return;

        if (activeTab.filePath == null || !activeTab.existsOnDisk) {
            // New file or file that doesn't exist on disk yet, needs "Save As"
            promptSaveAs(activeTab);
        } else {
            // Existing file, just save
            try {
                Files.writeString(activeTab.filePath, activeTab.content.get());
                activeTab.dirty = false;
                GroovyEngine.LOGGER.info("Saved script: {}", activeTab.filePath.getFileName());
                // After saving, ensure it's in the sidebar list if it wasn't there before
                if (!scriptPathsInSidebar.contains(activeTab.filePath)) {
                    refreshScriptList(true); // Force refresh to add the newly saved file
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
                // For simplicity, this will only save existing files. New files need explicit "Save Active"
                // which will prompt for "Save As".
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

    // --- Save As Popup Logic ---
    private static ImString newFileNameInput = new ImString(256);
    private static EditorTab tabForSaveAs = null; // Reference to the tab being saved via "Save As"

    private static void promptSaveAs(EditorTab tab) {
        tabForSaveAs = tab; // Set the tab to be saved
        ImGui.openPopup("SaveAsPopup");
        // Initialize with current display name, or a default for new files
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
                    proposedName += ".groovy"; // Ensure .groovy extension
                }
                // Resolve the new path relative to the root scripts directory
                Path newFilePath = SCRIPTS_ROOT_PATH.resolve(proposedName);

                if (Files.exists(newFilePath)) {
                    // TODO: Ask for overwrite confirmation (e.g., another popup)
                    GroovyEngine.LOGGER.warn("File {} already exists. Overwriting.", newFilePath.getFileName());
                }

                try {
                    Files.createDirectories(newFilePath.getParent()); // Ensure all parent directories exist
                    Files.writeString(newFilePath, tabForSaveAs.content.get());

                    // Update the tab's properties after successful save
                    tabForSaveAs.filePath = newFilePath;
                    tabForSaveAs.displayName = SCRIPTS_ROOT_PATH.relativize(newFilePath).toString().replace(File.separatorChar, '/'); // Update display name for nested structure
                    tabForSaveAs.existsOnDisk = true;
                    tabForSaveAs.dirty = false;
                    GroovyEngine.LOGGER.info("Saved script as: {}", newFilePath.getFileName());
                    ImGui.closeCurrentPopup();
                    tabForSaveAs = null; // Clear the reference
                    refreshScriptList(true); // Update sidebar to show the new file
                } catch (IOException e) {
                    GroovyEngine.LOGGER.error("Failed to save script as {}: {}", newFilePath.getFileName(), e.getMessage());
                    // TODO: Show an ImGui error popup
                }
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
                tabForSaveAs = null; // Clear the reference
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

        // Check if already open
        for (EditorTab tab : openTabs) {
            if (Objects.equals(tab.filePath, scriptPath)) {
                activeTab = tab; // Activate existing tab
                return;
            }
        }

        // It's a new file to open, read content
        try {
            String content = Files.readString(scriptPath);
            // Use the relative path for the tab's display name
            String relativeDisplayName = SCRIPTS_ROOT_PATH.relativize(scriptPath).toString().replace(File.separatorChar, '/');
            EditorTab newTab = new EditorTab(scriptPath, relativeDisplayName, content, true);
            openTabs.add(newTab);
            activeTab = newTab; // Make this the active tab
            GroovyEngine.LOGGER.info("Opened script: {}", scriptPath.getFileName());
        } catch (IOException e) {
            GroovyEngine.LOGGER.error("Failed to read script {}: {}", scriptPath.getFileName(), e.getMessage());
            // TODO: Show an ImGui error popup
        }
    }

    // Force: true to always refresh, false to refresh only if scriptFiles is empty
    private static void refreshScriptList(boolean force) {
        if (!force && !scriptPathsInSidebar.isEmpty()) return; // Don't refresh if not forced and already populated

        scriptPathsInSidebar.clear(); // Clear existing list

        try {
            Files.createDirectories(SCRIPTS_ROOT_PATH); // Ensure directory exists
            // Walk the file tree to include subdirectories
            // Integer.MAX_VALUE allows for unlimited depth
            try (Stream<Path> walk = Files.walk(SCRIPTS_ROOT_PATH, Integer.MAX_VALUE)) {
                List<Path> foundPaths = walk
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".groovy"))
                        .collect(Collectors.toList());
                scriptPathsInSidebar.addAll(foundPaths);
                // Sort by relative path for better organization in the sidebar
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
                // Attempt to save the tab's content
                boolean saved = false;
                if (tabToConfirmClose.filePath == null || !tabToConfirmClose.existsOnDisk) {
                    // It's a new file, cannot save directly from this popup.
                    GroovyEngine.LOGGER.warn("Cannot save new/unsaved tab '{}' directly from close confirmation. Please use 'Save Active' and 'Save As'.", tabToConfirmClose.displayName);
                    // Instead of failing, we could open the Save As popup from here,
                    // but it complicates the popup logic (one modal opening another).
                    // For now, let the user know and keep the confirmation popup open.
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
                tabToConfirmClose = null; // Clear pending tab
                // If the user cancelled, they likely want to return to that tab
                if (activeTab != tabToConfirmClose) { // Prevent setting if already active
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
            tab.content.clear(); // Free ImString resources

            // Adjust active tab if the closed tab was active
            if (tab == activeTab) {
                if (!openTabs.isEmpty()) {
                    // Select previous tab if available, otherwise the first one
                    activeTab = openTabs.get(Math.max(0, index - 1));
                } else {
                    activeTab = null;
                }
            }
        }
    }
}