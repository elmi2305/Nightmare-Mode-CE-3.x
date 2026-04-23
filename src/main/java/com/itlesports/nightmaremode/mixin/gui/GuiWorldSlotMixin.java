package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.util.NMConfUtils;
import com.itlesports.nightmaremode.util.interfaces.GuiSelectWorldExt;
import com.itlesports.nightmaremode.util.interfaces.GuiWorldSlotExt;
import com.itlesports.nightmaremode.util.interfaces.SaveFormatExt;
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
import java.util.HashMap;
import java.util.Map;

@Mixin(GuiWorldSlot.class)
public abstract class GuiWorldSlotMixin extends GuiSlot implements GuiWorldSlotExt {
    private Map<Integer, int[]> starHitboxes = new HashMap<>();  // Tracks star positions

    @Shadow @Final GuiSelectWorld parentWorldGui;
    @Unique private static final ResourceLocation WORLD_BASIC = new ResourceLocation("nightmare:textures/gui/world_basic.png");

    public GuiWorldSlotMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }

    @Inject(method = "drawSlot", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureConfigAndDrawAccordingText(int worldTextIndex, int xPos, int yPos, int par5, Tessellator tess, CallbackInfo ci, SaveFormatComparator sfc){
        SaveFormatExt sfcExt = (SaveFormatExt) (sfc);

        int starX = xPos - 60;
        int starY = yPos + 10;
        int starSize = 12;

        // we put the hitboxes here
        starHitboxes.put(worldTextIndex, new int[]{starX, starY, starSize, starSize});

        boolean isFavorited = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$isFavorited(sfc.getFileName());
        boolean isHovered = isMouseOverStar(starX, starY, starSize);
        boolean isSelected = this.isSelected(worldTextIndex);
        boolean isHoveredOverWorld = this.isMouseOverWorld(starX, starY, starSize);

        // when to draw it
        if (isFavorited || isSelected || isHovered || isHoveredOverWorld) {
            drawStar(starX, starY, starSize, isFavorited);
        }


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

        // build the existing game-mode text (var9) exactly like vanilla so spacing matches
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

        // config string to render (e.g. BM+TE+BS)
        String confString = NMConfUtils.getTextForActiveConfig(confArray);
        FontRenderer font = this.parentWorldGui.fontRenderer;

        // draw the comma after the existing text (same position as before)
        int baseTextX = xPos + 2 + font.getStringWidth(var9);
        int textX = baseTextX + 5;
        int textY = yPos + 12 + 10;
        this.parentWorldGui.drawString(font, ",", baseTextX, textY, 8421504);

        final int LIST_CONTENT_WIDTH = 220;
        final int SCROLLBAR_PADDING = 6;

        int listRight = xPos + LIST_CONTENT_WIDTH - SCROLLBAR_PADDING;
        int maxWidth = listRight - textX;

        // if there's essentially no room, draw the text normally (no scroll)
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

        // scrolling parameters
        final int paddingBetweenLoops = 20;
        final float speedPixelsPerSecond = 40f;
        final int pauseMs = 2000;

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

        ScaledResolution sr;
        int scaleFactor;
        try {
            sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            scaleFactor = Math.max(1, sr.getScaleFactor());
        } catch (Throwable e) {
            // if anything odd happens, fall back to a safe scale of 1
            scaleFactor = 1;
        }

        int scissorX = textX * scaleFactor;
        int scissorW = Math.max(1, maxWidth * scaleFactor);
        int scissorH = Math.max(1, font.FONT_HEIGHT * scaleFactor);
        int scissorY = mc.displayHeight - ((textY + font.FONT_HEIGHT) * scaleFactor);

        if (scissorX < 0) scissorX = 0;
        if (scissorY < 0) scissorY = 0;
        if (scissorW > mc.displayWidth - scissorX) scissorW = mc.displayWidth - scissorX;
        if (scissorH > mc.displayHeight - scissorY) scissorH = mc.displayHeight - scissorY;

        if (scissorW <= 0 || scissorH <= 0) {
            this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            return;
        }

        boolean scissorWasEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        try {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

            // primary copy
            this.parentWorldGui.drawString(font, confString, drawX, textY, 0xFF0000);

            int secondCopyX = drawX + textWidth + paddingBetweenLoops;
            if (secondCopyX < textX + maxWidth && secondCopyX + textWidth > textX) {
                this.parentWorldGui.drawString(font, confString, secondCopyX, textY, 0xFF0000);
            }
        } catch (Throwable e) {
            try {
                this.parentWorldGui.drawString(font, confString, textX, textY, 0xFF0000);
            } catch (Throwable ignored) {}
        } finally {
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


    @Unique
    private boolean isMouseOverStar(int starX, int starY, int starSize) {
        int mouseX = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$getLastMouseX();
        int mouseY = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$getLastMouseY();

        return mouseX >= starX && mouseX <= starX + starSize &&
                mouseY >= starY && mouseY <= starY + starSize;
    }
    @Unique
    private boolean isMouseOverWorld(int starX, int starY, int starSize) {
        int xOffset = 280;
        int yOffset = 11;
        int mouseX = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$getLastMouseX();
        int mouseY = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$getLastMouseY();

        return mouseX >= starX && mouseX <= starX + starSize + xOffset &&
                mouseY >= starY - yOffset && mouseY <= starY + starSize + yOffset;
    }

    @Unique
    private void drawStar(int textX, int textY, int size, boolean filled) {
        // Use Unicode star character for simplicity
        int color = filled ? 0xFFFFAA00 : 0xFF888888; // Gold for filled, gray for outline
        int outlineColor = 0xFFFFFFFF;

        int radius = 6;
        float outerR = radius;
        float innerR = radius * 0.5f;
        float cx = (float) textX + (float) radius / 2 + 4;
        float cy = (float) textY + (float) radius / 2 + 4;

        Tessellator t = Tessellator.instance;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_CULL_FACE);

        // --- Fill (only if active) ---
        GL11.glColor4f(
                ((color >> 16) & 255) / 255f,
                ((color >> 8) & 255) / 255f,
                (color & 255) / 255f,
                1.0f
        );

        t.startDrawing(GL11.GL_TRIANGLE_FAN);
        t.addVertex(cx, cy, 0);

        for (int i = 0; i <= 10; i++) {
            float angle = (float) (Math.PI * 2 * i / 10.0 - Math.PI / 2);
            float r = (i % 2 == 0) ? outerR : innerR;
            float x = cx + (float) Math.cos(angle) * r;
            float y = cy + (float) Math.sin(angle) * r;
            t.addVertex(x, y, 0);
        }

        t.draw();

        // --- Outline ---
        GL11.glLineWidth(1.5f);
        GL11.glColor4f(
                ((outlineColor >> 16) & 255) / 255f,
                ((outlineColor >> 8) & 255) / 255f,
                (outlineColor & 255) / 255f,
                1.0f
        );

        t.startDrawing(GL11.GL_LINE_LOOP);

        for (int i = 0; i < 10; i++) {
            float angle = (float) (Math.PI * 2 * i / 10.0 - Math.PI / 2);
            float r = (i % 2 == 0) ? outerR : innerR;
            float x = cx + (float) Math.cos(angle) * r;
            float y = cy + (float) Math.sin(angle) * r;
            t.addVertex(x, y, 0);
        }

        t.draw();

        // restore state
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);


//        this.parentWorldGui.drawString(this.parentWorldGui.fontRenderer, starChar, textX, textY, color);
    }


    @Override
    public int nightmareMode$getStarClicked(double mouseX, double mouseY) {
        // Check each tracked hitbox to see if the mouse click is within it
        for (Map.Entry<Integer, int[]> entry : starHitboxes.entrySet()) {
            int[] box = entry.getValue();
            if (mouseX >= box[0] && mouseX <= box[0] + box[2] &&
                    mouseY >= box[1] && mouseY <= box[1] + box[3]) {
                return entry.getKey();
            }
        }
        return -1;
    }
}