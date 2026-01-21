package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.NMConfUtils;
import com.itlesports.nightmaremode.SaveFormatExt;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;

@Mixin(GuiWorldSlot.class)
public abstract class GuiWorldSlotMixin extends GuiSlot {


    @Shadow @Final GuiSelectWorld parentWorldGui;
    private static final ResourceLocation WORLD_BASIC = new ResourceLocation("textures/gui/world_basic.png");
    private static final ResourceLocation WORLD_BLOODMOON = new ResourceLocation("textures/gui/world_bloodmoon_layer.png");
    private static final ResourceLocation WORLD_ECLIPSE = new ResourceLocation("textures/gui/world_eclipse_layer.png");

    public GuiWorldSlotMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }


    @Inject(method = "drawSlot", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureConfigAndDrawAccordingText(int worldTextIndex, int xPos, int yPos, int par5, Tessellator tess, CallbackInfo ci, SaveFormatComparator sfc){

        SaveFormatExt sfcExt = (SaveFormatExt) (sfc);
        int[] confArray = sfcExt.nightmareMode$getConfArray();

        GuiWorldSlot self = (GuiWorldSlot) (Object)this;


        // draw world icon
        int size = 32;
        int x = xPos - 35;

        Minecraft mc = Minecraft.getMinecraft();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        mc.getTextureManager().bindTexture(WORLD_BASIC);

        tess.startDrawingQuads();
        tess.addVertexWithUV(x, yPos + size, 0, 0, 1);
        tess.addVertexWithUV(x + size, yPos + size, 0, 1, 1);
        tess.addVertexWithUV(x + size, yPos,   0, 1, 0);
        tess.addVertexWithUV(x, yPos, 0, 0, 0);
        tess.draw();

        if (!Arrays.stream(confArray).anyMatch(a -> a == 1)) return;

        // Build the existing game-mode text (var9) exactly like vanilla so spacing matches
        String var9 = "";
        if (sfc.requiresConversion()) {
            var9 = GuiSelectWorld.func_82311_i(this.parentWorldGui) + " " + var9;
        } else {
            var9 = GuiSelectWorld.func_82314_j(this.parentWorldGui)[sfc.getEnumGameType().getID()];
            if (sfc.isHardcoreModeEnabled()) {
                var9 = EnumChatFormatting.DARK_RED + I18n.getString("gameMode.hardcore") + EnumChatFormatting.RESET;
            }

            if (sfc.getCheatsEnabled()) {
                var9 = var9 + ", " + I18n.getString("selectWorld.cheats");
            }
        }

        // Config string to render (e.g. BM+TE+BS)
        String confString = NMConfUtils.getTextForActiveConfig(confArray);
        FontRenderer font = this.parentWorldGui.fontRenderer;

        // Draw the comma after the existing text (same position as before)
        int baseTextX = xPos + 2 + font.getStringWidth(var9);
        int textX = baseTextX + 5;
        int textY = yPos + 12 + 10;
        this.parentWorldGui.drawString(font, ",", baseTextX, textY, 8421504);

        // === Determine available width (list-relative so resizing doesn't break it) ===
        // This is a conservative list width used by GuiSelectWorld. Adjust slightly if your layout differs.
        final int LIST_CONTENT_WIDTH = 220; // safe default used by many GuiSelectWorld layouts
        final int SCROLLBAR_PADDING = 6;     // padding before the scrollbar

        int listRight = xPos + LIST_CONTENT_WIDTH - SCROLLBAR_PADDING;
        int maxWidth = listRight - textX;

        // Defensive: if there's essentially no room, draw the text normally (no scroll)
        if (maxWidth <= 4) {
            this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            return;
        }

        int textWidth = font.getStringWidth(confString);

        // If it fits, draw normally
        if (textWidth <= maxWidth) {
            this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            return;
        }

        // === Scrolling / marquee parameters ===
        final int paddingBetweenLoops = 20;       // gap in pixels between the end and the start when looping
        final float speedPixelsPerSecond = 40f;   // scrolling speed
        final int pauseMs = 2000;                 // pause at start/end in ms

        int totalScrollPixels = textWidth + paddingBetweenLoops;
        long scrollDurationMs = (long) ((totalScrollPixels / speedPixelsPerSecond) * 1000.0);
        long cycleMs = pauseMs + scrollDurationMs + pauseMs;

        long now = System.currentTimeMillis();
        long t = now % cycleMs;

        int scrollOffsetPx;
        if (t < pauseMs) {
            scrollOffsetPx = 0;
        } else if (t >= (pauseMs + scrollDurationMs)) {
            scrollOffsetPx = totalScrollPixels;
        } else {
            long scrollTime = t - pauseMs;
            scrollOffsetPx = (int) ((scrollTime / 1000.0) * speedPixelsPerSecond);
            if (scrollOffsetPx < 0) scrollOffsetPx = 0;
            if (scrollOffsetPx > totalScrollPixels) scrollOffsetPx = totalScrollPixels;
        }

        int drawX = textX - scrollOffsetPx;

        // === Scissor / clipping: use ScaledResolution for correct scale and be defensive ===
        if (mc == null) { // defensive: unlikely but safe
            this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            return;
        }

        ScaledResolution sr;
        int scaleFactor;
        try {
            // Use ScaledResolution so we get the same scale Minecraft uses for GUI rendering.
            // Constructor shape may vary across versions; the common form is (Minecraft, displayWidth, displayHeight).
            sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            scaleFactor = Math.max(1, sr.getScaleFactor());
        } catch (Throwable e) {
            // If anything odd happens, fall back to a safe scale of 1
            scaleFactor = 1;
        }

        // Convert GUI coordinates -> real framebuffer pixels for glScissor
        int scissorX = textX * scaleFactor;
        int scissorW = Math.max(1, maxWidth * scaleFactor);
        // OpenGL's scissor Y origin is bottom-left, Minecraft's GUI origin is top-left
        int scissorH = Math.max(1, font.FONT_HEIGHT * scaleFactor);
        int scissorY = mc.displayHeight - ((textY + font.FONT_HEIGHT) * scaleFactor);

        // Safety clamp to framebuffer bounds (avoid negative or out-of-range rects)
        if (scissorX < 0) scissorX = 0;
        if (scissorY < 0) scissorY = 0;
        if (scissorW > mc.displayWidth - scissorX) scissorW = mc.displayWidth - scissorX;
        if (scissorH > mc.displayHeight - scissorY) scissorH = mc.displayHeight - scissorY;

        if (scissorW <= 0 || scissorH <= 0) {
            // Invalid scissor—fallback safe draw
            this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            return;
        }

        // === Draw with GL scissoring, preserving GL state and recovering on errors ===
        boolean scissorWasEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        try {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

            // primary copy
            this.parentWorldGui.drawString(font, confString, drawX, textY, 0xFF0000);

            // second copy to ensure seamless looping — only draw if it might be visible
            int secondCopyX = drawX + textWidth + paddingBetweenLoops;
            if (secondCopyX < textX + maxWidth && secondCopyX + textWidth > textX) {
                this.parentWorldGui.drawString(font, confString, secondCopyX, textY, 0xFF0000);
            }
        } catch (Throwable e) {
            // On any GL problem, fallback to safe single draw
            try {
                this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            } catch (Throwable ignored) {}
        } finally {
            // Restore GL scissor state to how we found it
            if (!scissorWasEnabled) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }

    @Unique private void drawLayer(Tessellator tess, int x, int yPos){
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, yPos + 32, 0, 0, 1);
        tess.addVertexWithUV(x + 32, yPos + 32, 0, 1, 1);
        tess.addVertexWithUV(x + 32, yPos,   0, 1, 0);
        tess.addVertexWithUV(x, yPos, 0, 0, 0);
        tess.draw();
    }
}