package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityUnderforge;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiUnderforge extends GuiContainer {
    private static final ResourceLocation CHEST_TEXTURE =
            new ResourceLocation("textures/gui/container/generic_54.png");
    private final TileEntityUnderforge underforge;

    public GuiUnderforge(InventoryPlayer playerInventory, TileEntityUnderforge underforge) {
        super(new ContainerUnderforge(playerInventory, underforge));
        this.underforge = underforge;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = StatCollector.translateToLocal(this.underforge.getInvName());
        this.fontRenderer.drawString(title, 8, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(CHEST_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, 71);
        this.drawTexturedModalRect(x, y + 70, 0, 126, this.xSize, 96);
    }
}
