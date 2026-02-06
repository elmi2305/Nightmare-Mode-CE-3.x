package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiLocker extends GuiContainer {

    private static final ResourceLocation TEX = new ResourceLocation("textures/gui/steel_locker_gui.png");

    private static final int GUI_WIDTH = 356;
    private static final int GUI_HEIGHT = 240;

    private static final int MAIN_BG_X = 0, MAIN_BG_Y = 0, MAIN_BG_W = 356, MAIN_BG_H = 150;

    private static final int PLAYER_BG_X = 90, PLAYER_BG_Y = 150, PLAYER_BG_W = 176, PLAYER_BG_H = 90;

    private static final int CHEST_SLOT_X = 8;
    private static final int PLAYER_SLOT_X = 97;
    private static final int PLAYER_SLOT_Y = 157;

    private final IInventory inv;

    public GuiLocker(InventoryPlayer playerInv, IInventory inv) {
        super(new ContainerSteelLocker(playerInv, inv));
        this.inv = inv;
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String titleKey = inv.getInvName();
        String title = inv.isInvNameLocalized() ? titleKey : I18n.getString(titleKey);
        fontRenderer.drawString(title, CHEST_SLOT_X, 6, 0x404040);

        fontRenderer.drawString(I18n.getString("container.inventory"),
                PLAYER_SLOT_X, PLAYER_SLOT_Y - 12, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mx, int my) {
        GL11.glColor4f(1,1,1,1);
        mc.renderEngine.bindTexture(TEX);
        int gl = (width  - xSize)/2;
        int gt = (height - ySize)/2;

        blit(gl + MAIN_BG_X, gt + MAIN_BG_Y, MAIN_BG_X, MAIN_BG_Y, MAIN_BG_W, MAIN_BG_H);
        blit(gl + PLAYER_BG_X, gt + PLAYER_BG_Y, PLAYER_BG_X, PLAYER_BG_Y, PLAYER_BG_W, PLAYER_BG_H);
    }

    private void blit(int x,int y,int u,int v,int w,int h){
        float fW = 1f / 512f;
        float fH = 1f / 512f;
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x,     y+h, zLevel, u*fW,(v+h)*fH);
        t.addVertexWithUV(x+w,   y+h, zLevel,(u+w)*fW,(v+h)*fH);
        t.addVertexWithUV(x+w,   y,   zLevel,(u+w)*fW, v*fH);
        t.addVertexWithUV(x,     y,   zLevel, u*fW, v*fH);
        t.draw();
    }
}