package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.MinerDrillTileEntity;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.I18n;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMinerDrill extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nightmare:textures/gui/horseGui.png");

    public GuiMinerDrill(InventoryPlayer playerInventory, MinerDrillTileEntity drill) {
        super(new ContainerMinerDrill(playerInventory, drill));
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.getString("container.ifhyMinerDrill"), 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, 72, 0x404040);
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
