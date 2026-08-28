package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.util.interfaces.IArmorStatus;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @Shadow private void renderQuad(Tessellator tessellator, int x, int y, int width, int height, int color) {}

    @ModifyArgs(method = "renderItemIntoGUI", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", remap = false))
    private void changeBrightnessItem(Args args){
        float r = args.get(0);
        float g = args.get(1);
        float b = args.get(2);
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player != null && player.inGloomCounter > 0) {
            float darkness = getDarkness(player);
            args.set(0, r-darkness);
            args.set(1, g-darkness);
            args.set(2, b-darkness);
        }
    }
    @ModifyArg(method = "renderItemIntoGUI", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderBlocks;renderBlockAsItem(Lnet/minecraft/src/Block;IF)V"),index = 2)
    private float changeBrightnessBlock(float fBrightness){
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && player.inGloomCounter > 0) {
            float darkness = getDarkness(player);
            return fBrightness - darkness;
        }
        return fBrightness;
    }
    @Unique
    private float getDarkness(EntityPlayer player) {
            int gloomProgress = Math.max(player.inGloomCounter + (player.getGloomLevel() - 1) * 200, 0);
            float maxDarkness = 0.95F;
            float stageDarkness = Math.min((float)gloomProgress / 250.0f, 1.0f) * maxDarkness;
            return Math.min(stageDarkness, 1.0F);
    }

    @Inject(method = "renderItemOverlayIntoGUI(Lnet/minecraft/src/FontRenderer;Lnet/minecraft/src/TextureManager;Lnet/minecraft/src/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void renderCompressedAirBar(FontRenderer font, TextureManager textures, ItemStack stack,
                                        int x, int y, String text, CallbackInfo ci) {
        if (stack == null || !(stack.getItem() instanceof IArmorStatus status)) {
            return;
        }

        int width = MathHelper.clamp_int(Math.round(13.0F * status.getStatusFraction(stack)), 0, 13);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        this.renderQuad(tessellator, x + 2, y + 11, 13, 2, 0);
        this.renderQuad(tessellator, x + 2, y + 11, 12, 1, status.getStatusBackgroundColor());
        this.renderQuad(tessellator, x + 2, y + 11, width, 1, status.getStatusColor(stack));
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
