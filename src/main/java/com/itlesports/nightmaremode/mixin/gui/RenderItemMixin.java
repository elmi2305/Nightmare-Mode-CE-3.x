package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @ModifyArgs(method = "renderItemIntoGUI", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"))
    private void changeBrightness0(Args args){
        float r = args.get(0);
        float g = args.get(1);
        float b = args.get(2);
        float darkness = (getDarkness(Minecraft.getMinecraft().thePlayer));
        args.set(0, r-darkness);
        args.set(1, g-darkness);
        args.set(2, b-darkness);
    }
    @ModifyArg(method = "renderItemIntoGUI", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderBlocks;renderBlockAsItem(Lnet/minecraft/src/Block;IF)V"),index = 2)
    private float changeBrightnessBlock(float fBrightness){
        float darkness = (getDarkness(Minecraft.getMinecraft().thePlayer));
        return fBrightness - darkness;
    }
    @Unique
    private float getDarkness(EntityPlayer player) {
        int gloomProgress = player.inGloomCounter + (player.getGloomLevel() - 1) * 200;
        float maxDarkness = 0.95F;
        float stageDarkness = Math.min((float)gloomProgress / 400.0F, 1.0f) * maxDarkness;
        return Math.min(stageDarkness, 1.0F);
    }
}
