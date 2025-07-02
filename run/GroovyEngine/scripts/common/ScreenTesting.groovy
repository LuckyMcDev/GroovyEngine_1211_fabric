// Import statements that will be remapped by GroovyEngine
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult

// Define the custom screen class using Groovy syntax
class SimpleTextScreen extends Screen {
    SimpleTextScreen() {
        super(Text.literal("Simple Text Screen"))
    }

    @Override
    void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Hello from GroovyEngine!"),
                (int)(this.width / 2),
                (int)(this.height / 2),
                0xFFFFFF
        )
    }

    @Override
    boolean shouldCloseOnEsc() {
        return true
    }
}

// Script logic
def openScreenNextTick = true

TickEvents.onEndClientTick { ctx ->
    if (openScreenNextTick) {
        MinecraftClient.getInstance().setScreen(new SimpleTextScreen())
        openScreenNextTick = false
    }
}

PlayerEvents.onBlockBreak { ctx ->
    openScreenNextTick = true // mark for next client tick
    Logger.info("Open Screen?")
    return ActionResult.PASS
}