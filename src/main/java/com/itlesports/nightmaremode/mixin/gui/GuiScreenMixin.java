package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.nmgui.GuiJoiningWorld;
import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import com.itlesports.nightmaremode.util.interfaces.JourneyBrowserInput;
import net.minecraft.src.*;
import org.lwjgl.input.Mouse;
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

    /** Use the title screen's startup-selected texture anywhere vanilla draws its tiled dirt background. */
    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation journeyMode$replaceDirtBackground(ResourceLocation vanillaTexture) {
        return JourneyTitleTheme.getActive(Minecraft.getMinecraft()).background;
    }

    /** Keep the pre-world terrain screens visually continuous with the themed loader. */
    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Tessellator;setColorOpaque_I(I)V"))
    private int journeyMode$tintTerrainLoadingDirt(int vanillaColor) {
        GuiScreen screen = (GuiScreen) (Object) this;
        if (screen instanceof GuiDownloadTerrain || screen instanceof GuiJoiningWorld || screen instanceof GuiOptions) {
            return JourneyTitleTheme.getActive(Minecraft.getMinecraft()).buttonFill & 0x00FFFFFF;
        }
        return vanillaColor;
    }

    @ModifyArg(method = "drawWorldBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/GuiScreen;drawGradientRect(IIIIII)V"), index = 4)
    private int journeyMode$tintOptionsGradientTop(int vanillaColor) {
        GuiScreen screen = (GuiScreen) (Object) this;
        return screen instanceof GuiOptions
                ? JourneyTitleTheme.getActive(Minecraft.getMinecraft()).cardFill
                : vanillaColor;
    }

    @ModifyArg(method = "drawWorldBackground", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/GuiScreen;drawGradientRect(IIIIII)V"), index = 5)
    private int journeyMode$tintOptionsGradientBottom(int vanillaColor) {
        GuiScreen screen = (GuiScreen) (Object) this;
        return screen instanceof GuiOptions
                ? JourneyTitleTheme.getActive(Minecraft.getMinecraft()).buttonFill
                : vanillaColor;
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawGlobalDarkness(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;
        GuiScreen thisObj = (GuiScreen)(Object)this;
        if (!(thisObj instanceof GuiInventory)) return; // only darken inventory

        EntityPlayer player = mc.thePlayer;
        int inGloomCounter = player.inGloomCounter;
        int gloomLevel = player.getGloomLevel() - 1;

        int progress = Math.max(inGloomCounter + (gloomLevel * 200), 0);
        int maxProgress = 250;

        if (progress > 0) {
            float darkness = Math.min(progress / (float) maxProgress, 1.0f) * 0.95f;
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

    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    private void passJourneyBrowserWheel(CallbackInfo ci) {
        GuiScreen screen = (GuiScreen) (Object) this;
        if (!(screen instanceof JourneyBrowserInput)) return;
        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) return;
        Minecraft mc = Minecraft.getMinecraft();
        int mouseX = Mouse.getEventX() * this.width / mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
        ((JourneyBrowserInput) screen).nightmareMode$handleJourneyBrowserWheel(mouseX, mouseY, wheel);
    }

    @Inject(method = "mouseClickMove", at = @At("HEAD"))
    private void passJourneyBrowserDrag(int mouseX, int mouseY, int button, long heldTime, CallbackInfo ci) {
        GuiScreen screen = (GuiScreen) (Object) this;
        if (screen instanceof JourneyBrowserInput) ((JourneyBrowserInput) screen).nightmareMode$handleJourneyBrowserDrag(mouseX, mouseY, button);
    }

    @Inject(method = "mouseMovedOrUp", at = @At("HEAD"))
    private void releaseJourneyBrowserDrag(int mouseX, int mouseY, int button, CallbackInfo ci) {
        GuiScreen screen = (GuiScreen) (Object) this;
        if (screen instanceof JourneyBrowserInput) ((JourneyBrowserInput) screen).nightmareMode$releaseJourneyBrowserMouse(mouseX, mouseY, button);
    }
}
