package com.itlesports.nightmaremode.nmgui;


import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiAdvancedHorseArmor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/horseGui.png");
    private final IInventory armorInv;

    public GuiAdvancedHorseArmor(InventoryPlayer playerInv, IInventory armorInv) {
        super(new ContainerHorseArmor(playerInv, armorInv));
        this.armorInv = armorInv;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.getString("gui.nm.horseMenu"), 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

}