package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.EnderAssemblerTileEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiEnderAssembler extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final EnderAssemblerTileEntity assembler;

    public GuiEnderAssembler(InventoryPlayer inventory, EnderAssemblerTileEntity assembler) {
        super(new ContainerEnderAssembler(inventory, assembler));
        this.assembler = assembler;
        this.ySize = 166;
    }
    @Override protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRenderer.drawString(I18n.getString("container.ifhyEnderAssembler"), 8, 6, 0x404040);
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, 72, 0x404040);
        int total = Math.max(1, this.assembler.getProcessTotal());
        this.fontRenderer.drawString((this.assembler.getProcessTicks() * 100 / total) + "%", 119, 38, 0x404040);
    }
    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int left = (this.width - this.xSize) / 2, top = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
    }
}
