package io.github.luckymcdev.mixin.editor.input;

import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (ImGuiImpl.getIO().getWantCaptureKeyboard()) {
            ci.cancel(); // Prevent hotbar, movement, etc.
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (ImGuiImpl.getIO().getWantCaptureKeyboard()) {
            ci.cancel(); // Prevent character input
        }
    }
}

