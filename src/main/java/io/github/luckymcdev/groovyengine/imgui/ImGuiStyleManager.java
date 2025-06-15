package io.github.luckymcdev.groovyengine.imgui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;

public class ImGuiStyleManager {
    public static void setupCustomGrayTheme() {
        ImGuiStyle style = ImGui.getStyle();

        // Text colors - improved contrast
        style.setColor(ImGuiCol.Text,                    0.95f, 0.95f, 0.95f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled,            0.60f, 0.60f, 0.60f, 1.00f);

        // Window backgrounds - refined grays
        style.setColor(ImGuiCol.WindowBg,                0.12f, 0.12f, 0.12f, 0.95f);
        style.setColor(ImGuiCol.ChildBg,                 0.08f, 0.08f, 0.08f, 0.20f);
        style.setColor(ImGuiCol.PopupBg,                 0.10f, 0.10f, 0.10f, 0.96f);

        // Borders - cleaner look
        style.setColor(ImGuiCol.Border,                  0.40f, 0.40f, 0.40f, 0.60f);
        style.setColor(ImGuiCol.BorderShadow,            0.00f, 0.00f, 0.00f, 0.00f);

        // Frame backgrounds - better hierarchy
        style.setColor(ImGuiCol.FrameBg,                 0.18f, 0.18f, 0.18f, 0.85f);
        style.setColor(ImGuiCol.FrameBgHovered,          0.22f, 0.22f, 0.22f, 0.90f);
        style.setColor(ImGuiCol.FrameBgActive,           0.28f, 0.28f, 0.28f, 0.95f);

        // Title bars - consistent dark theme
        style.setColor(ImGuiCol.TitleBg,                 0.08f, 0.08f, 0.08f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive,           0.12f, 0.12f, 0.12f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed,        0.06f, 0.06f, 0.06f, 0.80f);

        // Menu bar - subtle but distinct
        style.setColor(ImGuiCol.MenuBarBg,               0.16f, 0.16f, 0.16f, 1.00f);

        // Scrollbars - improved visibility
        style.setColor(ImGuiCol.ScrollbarBg,             0.08f, 0.08f, 0.08f, 0.60f);
        style.setColor(ImGuiCol.ScrollbarGrab,           0.35f, 0.35f, 0.35f, 0.80f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered,    0.45f, 0.45f, 0.45f, 0.90f);
        style.setColor(ImGuiCol.ScrollbarGrabActive,     0.55f, 0.55f, 0.55f, 1.00f);

        // Interactive elements - subtle green accent
        style.setColor(ImGuiCol.CheckMark,               0.40f, 0.70f, 0.40f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab,              0.45f, 0.45f, 0.45f, 0.80f);
        style.setColor(ImGuiCol.SliderGrabActive,        0.40f, 0.70f, 0.40f, 1.00f);

        // Buttons - modern flat design
        style.setColor(ImGuiCol.Button,                  0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.ButtonHovered,           0.26f, 0.26f, 0.26f, 1.00f);
        style.setColor(ImGuiCol.ButtonActive,            0.32f, 0.32f, 0.32f, 1.00f);

        // Headers - clean and consistent
        style.setColor(ImGuiCol.Header,                  0.25f, 0.25f, 0.25f, 0.70f);
        style.setColor(ImGuiCol.HeaderHovered,           0.30f, 0.30f, 0.30f, 0.80f);
        style.setColor(ImGuiCol.HeaderActive,            0.35f, 0.35f, 0.35f, 0.90f);

        // Separators - subtle blue accent
        style.setColor(ImGuiCol.Separator,               0.30f, 0.30f, 0.30f, 1.00f);
        style.setColor(ImGuiCol.SeparatorHovered,        0.20f, 0.50f, 0.80f, 0.80f);
        style.setColor(ImGuiCol.SeparatorActive,         0.20f, 0.50f, 0.80f, 1.00f);

        // Resize grips - improved visibility
        style.setColor(ImGuiCol.ResizeGrip,              0.25f, 0.25f, 0.25f, 0.60f);
        style.setColor(ImGuiCol.ResizeGripHovered,       0.40f, 0.40f, 0.40f, 0.80f);
        style.setColor(ImGuiCol.ResizeGripActive,        0.60f, 0.60f, 0.60f, 1.00f);

        // Tabs - modern tab design
        style.setColor(ImGuiCol.Tab,                     0.16f, 0.16f, 0.16f, 0.90f);
        style.setColor(ImGuiCol.TabHovered,              0.24f, 0.24f, 0.24f, 1.00f);
        style.setColor(ImGuiCol.TabActive,               0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.TabUnfocused,            0.12f, 0.12f, 0.12f, 0.80f);
        style.setColor(ImGuiCol.TabUnfocusedActive,      0.16f, 0.16f, 0.16f, 0.90f);

        // Docking - consistent with theme
        style.setColor(ImGuiCol.DockingPreview,          0.20f, 0.50f, 0.80f, 0.40f);
        style.setColor(ImGuiCol.DockingEmptyBg,          0.08f, 0.08f, 0.08f, 1.00f);

        // Plots - improved colors
        style.setColor(ImGuiCol.PlotLines,               0.70f, 0.70f, 0.70f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered,        0.90f, 0.60f, 0.40f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram,           0.60f, 0.80f, 0.40f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered,    0.80f, 0.90f, 0.60f, 1.00f);

        // Tables - clean table design
        style.setColor(ImGuiCol.TableHeaderBg,           0.16f, 0.16f, 0.16f, 1.00f);
        style.setColor(ImGuiCol.TableBorderStrong,       0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.TableBorderLight,        0.25f, 0.25f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.TableRowBg,              0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt,           0.18f, 0.18f, 0.18f, 0.20f);

        // Selection and highlighting
        style.setColor(ImGuiCol.TextSelectedBg,          0.20f, 0.50f, 0.80f, 0.50f);
        style.setColor(ImGuiCol.DragDropTarget,          0.40f, 0.70f, 0.40f, 0.80f);
        style.setColor(ImGuiCol.NavHighlight,            0.20f, 0.50f, 0.80f, 1.00f);
        style.setColor(ImGuiCol.NavWindowingHighlight,   0.90f, 0.90f, 0.90f, 0.80f);
        style.setColor(ImGuiCol.NavWindowingDimBg,       0.00f, 0.00f, 0.00f, 0.60f);
        style.setColor(ImGuiCol.ModalWindowDimBg,        0.00f, 0.00f, 0.00f, 0.70f);

        // Spacing and padding - improved layout
        style.setWindowPadding(8f, 8f);
        style.setFramePadding(12f, 6f);
        style.setCellPadding(6f, 4f);
        style.setItemSpacing(8f, 4f);
        style.setItemInnerSpacing(6f, 4f);
        style.setTouchExtraPadding(0f, 0f);
        style.setIndentSpacing(18f);
        style.setScrollbarSize(12f);
        style.setGrabMinSize(12f);

        // Border sizes - cleaner look
        style.setWindowBorderSize(0f);
        style.setChildBorderSize(0f);
        style.setPopupBorderSize(1f);
        style.setFrameBorderSize(0f);
        style.setTabBorderSize(0f);

        // Rounding - modern rounded corners
        style.setWindowRounding(4f);
        style.setChildRounding(3f);
        style.setFrameRounding(3f);
        style.setPopupRounding(4f);
        style.setScrollbarRounding(6f);
        style.setGrabRounding(3f);
        style.setLogSliderDeadzone(4f);
        style.setTabRounding(3f);

        // Alignment - better centering
        style.setWindowTitleAlign(0.5f, 0.5f);
        style.setButtonTextAlign(0.5f, 0.5f);
        style.setSelectableTextAlign(0f, 0.5f);
        style.setDisplaySafeAreaPadding(4f, 4f);

        // Anti-aliasing and quality
        style.setAntiAliasedLines(true);
        style.setAntiAliasedLinesUseTex(true);
        style.setAntiAliasedFill(true);
        style.setCurveTessellationTol(1.25f);
        style.setCircleTessellationMaxError(0.30f);

        // Menu button position
        style.setWindowMenuButtonPosition(ImGuiDir.Left);
    }
}