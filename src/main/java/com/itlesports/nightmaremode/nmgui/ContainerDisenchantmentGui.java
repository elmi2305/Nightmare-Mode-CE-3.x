package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityDisenchantmentTable;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class ContainerDisenchantmentGui extends GuiContainer {
    private static final ResourceLocation FREEZER_TEXTURE = new ResourceLocation("nightmare:textures/gui/guiDisenchantment.png");
    public TileEntityDisenchantmentTable tileEntity;

    public ContainerDisenchantmentGui(InventoryPlayer par1InventoryPlayer, TileEntityDisenchantmentTable par2TileEntityDispenser)
    {
        super(new ContainerDisenchantment(par1InventoryPlayer, par2TileEntityDispenser));
        this.ySize = 210;
        this.tileEntity = par2TileEntityDispenser;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String title = this.tileEntity.func_94133_a();
        this.fontRenderer.drawString(title, 60, 6, 0x404040);

        int cost = this.tileEntity.totalCost;

        if (cost > 0) {
            String costText = "Cost: " + cost;
            if (cost == 1) costText = "Cost: 1 level";
            else costText = "Cost: " + cost + " levels";

            int textX = this.xSize - this.fontRenderer.getStringWidth(costText) - 8;
            int textY = this.ySize - 95;

            this.fontRenderer.drawStringWithShadow(costText, textX, textY, 0x80FF20);

        } else {

            String hint = "Insert enchanted item & book";
            int textX = this.xSize - this.fontRenderer.getStringWidth(hint) - 8;
            int textY = this.ySize - 95;
            this.fontRenderer.drawString(hint, textX, textY, 0x202020);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(FREEZER_TEXTURE);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
    }

}
