package io.github.luckymcdev.groovyengine.imgui;

import com.mojang.blaze3d.platform.GlStateManager;
import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ImGuiImpl {
    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private static ImFont defaultFont;


    public static ImGuiIO getIO() {
        return ImGui.getIO();
    }

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();

        ImGuiStyleManager.setupCustomGrayTheme();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename("groovyengine.ini");
        data.setFontGlobalScale(1F);

        {
            final ImFontAtlas fonts = data.getFonts();
            final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();

            // Add all required glyph sets
            rangesBuilder.addRanges(fonts.getGlyphRangesDefault());
            rangesBuilder.addRanges(fonts.getGlyphRangesCyrillic());
            rangesBuilder.addRanges(fonts.getGlyphRangesJapanese());
            short[] glyphRanges = rangesBuilder.buildRanges();

            try {
                // Load font TTF from your mod's resources
                byte[] fontData = IOUtils.toByteArray(Objects.requireNonNull(
                        ImGuiImpl.class.getResourceAsStream("/assets/groovyengine/fonts/JetBrainsMonoNerdFont-Bold.ttf")));

                ImFontConfig config = new ImFontConfig();
                config.setGlyphRanges(glyphRanges);
                config.setName("JetBrains Mono Nerd font bold");

                fonts.addFontFromMemoryTTF(fontData, 16f, config);
                defaultFont = fonts.addFontFromMemoryTTF(fontData, 16f, config);
                fonts.build();

                config.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
       }

        data.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    public static ImFont getDefaultFont() {
        return defaultFont;
    }


    public static void draw(final RenderInterface runnable) {

        // start frame
        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame(); // Handle keyboard and mouse interactions
        ImGui.newFrame();

        // do rendering logic
        runnable.render(ImGui.getIO());

        // end frame
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    public static void dispose() {
        imGuiImplGl3.shutdown();

        ImGui.destroyContext();
        ImPlot.destroyContext();
    }

    //Unused, load buffered images (was for the live shader view)
    public static int fromBufferedImage(BufferedImage image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = pixels[y * image.getWidth() + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        final int texture = GlStateManager._genTexture();
        GlStateManager._bindTexture(texture);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        return texture;
    }
}
