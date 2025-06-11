package io.github.luckymcdev.mixin.editor.input;

import imgui.ImGui;
import io.github.luckymcdev.impl.imgui.ImGuiImpl;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(long window, int button, int action, int mods, CallbackInfo ci) {
        if (ImGuiImpl.getIO().getWantCaptureMouse()) {
            ci.cancel(); // Block click input to game
        }
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void onMouseMove(long window, double x, double y, CallbackInfo ci) {
        if (ImGuiImpl.getIO().getWantCaptureMouse()) {
            ci.cancel(); // Block mouse movement (stops rotation)
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ImGuiImpl.getIO().getWantCaptureMouse()) {
            ci.cancel(); // Prevent scroll input
        }
    }
}

