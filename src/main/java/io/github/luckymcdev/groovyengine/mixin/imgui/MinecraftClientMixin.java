package io.github.luckymcdev.groovyengine.mixin.imgui;

import com.mojang.blaze3d.platform.Window;
import io.github.luckymcdev.groovyengine.imgui.ImGuiImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private Window window;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initImGui(GameConfig args, CallbackInfo ci) {
        ImGuiImpl.create(window.getWindow());
    }

    @Inject(method = "close", at = @At("RETURN"))
    public void closeImGui(CallbackInfo ci) {
        ImGuiImpl.dispose();
    }

}
