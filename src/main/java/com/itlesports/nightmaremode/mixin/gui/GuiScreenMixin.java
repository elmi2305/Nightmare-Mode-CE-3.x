package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @Shadow public int width;

    @Shadow public int height;

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation changeBackground(ResourceLocation par1ResourceLocation){
        if(NightmareMode.bloodmare){
            return new ResourceLocation("textures/gui/bloodNightmare.png");
        }
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawGlobalDarkness(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;
        GuiScreen thisObj = (GuiScreen)(Object)this;
        if (!(thisObj instanceof GuiInventory)) return; // only darken inventory

        EntityPlayer player = mc.thePlayer;
        int inGloomCounter = player.inGloomCounter;
        int gloomLevel = player.getGloomLevel();

        int progress = inGloomCounter + (gloomLevel * 200);
        int maxProgress = 400;

        if (progress > 0) {
            float darkness = progress / (float) maxProgress;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Tessellator tess = Tessellator.instance;
            tess.startDrawingQuads();
            tess.setColorRGBA_F(0f, 0f, 0f, darkness);
            tess.addVertex(0, this.height, 0);
            tess.addVertex(this.width, this.height, 0);
            tess.addVertex(this.width, 0, 0);
            tess.addVertex(0, 0, 0);
            tess.draw();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }
}
