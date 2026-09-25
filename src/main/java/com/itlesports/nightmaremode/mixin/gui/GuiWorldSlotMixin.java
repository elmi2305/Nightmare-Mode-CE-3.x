package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.mixin.interfaces.GuiSlotAccess;
import com.itlesports.nightmaremode.util.interfaces.GuiSelectWorldExt;
import com.itlesports.nightmaremode.util.interfaces.GuiWorldSlotExt;
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

import java.util.HashMap;
import java.util.Map;

@Mixin(GuiWorldSlot.class)
public abstract class GuiWorldSlotMixin extends GuiSlot implements GuiWorldSlotExt {
    @Unique private Map<Integer, int[]> starHitboxes = new HashMap<>();  // Tracks star positions
    @Unique private Map<Integer, int[]> folderPositions = new HashMap<>();  // Tracks folder positions

    @Shadow @Final GuiSelectWorld parentWorldGui;
    @Unique private static final ResourceLocation WORLD_BASIC = new ResourceLocation("nightmare:textures/gui/world_basic.png");
    @Unique private static final ResourceLocation FOLDER = new ResourceLocation("nightmare:textures/gui/folder.png");
    @Unique private static final ResourceLocation FOLDER_OPEN = new ResourceLocation("nightmare:textures/gui/folder_open.png");

    public GuiWorldSlotMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }

    @Inject(method = "drawSlot", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void drawWorldSlotDecorations(int worldTextIndex, int xPos, int yPos, int par5, Tessellator tess, CallbackInfo ci, SaveFormatComparator sfc){
        int starX = xPos - 60;
        int starY = yPos + 10;
        int starSize = 12;

        // we put the hitboxes here
        starHitboxes.put(worldTextIndex, new int[]{starX, starY, starSize, starSize});

        boolean isFavorited = ((GuiSelectWorldExt)(this.parentWorldGui)).nightmareMode$isFavorited(sfc.getFileName());
        boolean isHoveredStar = isMouseOverStar(starX, starY, starSize);
        boolean isSelected = this.isSelected(worldTextIndex);
        boolean isHoveredOverWorld = this.isMouseOverWorld(starX, starY, starSize);
        if (isFavorited || isSelected || isHoveredStar || isHoveredOverWorld) {
            drawStar(starX, starY, starSize, isFavorited);
        }

        // when to draw it
        if (NightmareMode.devMode) {
            int x1 = xPos - 80;
            int y1 = starY - 2;

            folderPositions.put(worldTextIndex, new int[]{x1, y1, 16, 16});

            boolean isHoveredFolder = isMouseOverStar(x1,y1, 16);
            if(isSelected){
                if(isHoveredFolder){
                    ((GuiSlotAccess)this).getMc().renderEngine.bindTexture(FOLDER_OPEN);
                }
                else {
                    ((GuiSlotAccess)this).getMc().renderEngine.bindTexture(FOLDER);
                }
                tess.startDrawingQuads();
                tess.addVertexWithUV(x1, y1 + 16, 0, 0, 1);
                tess.addVertexWithUV(x1 + 16, y1 + 16, 0, 1, 1);
                tess.addVertexWithUV(x1 + 16, y1,   0, 1, 0);
                tess.addVertexWithUV(x1, y1, 0, 0, 0);
                tess.draw();
            }
        }



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
    @Override
    public int nightmareMode$getFolderClicked(double mouseX, double mouseY) {
        // Check each tracked hitbox to see if the mouse click is within it
        for (Map.Entry<Integer, int[]> entry : folderPositions.entrySet()) {
            int[] box = entry.getValue();
            if (mouseX >= box[0] && mouseX <= box[0] + box[2] &&
                    mouseY >= box[1] && mouseY <= box[1] + box[3]) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
