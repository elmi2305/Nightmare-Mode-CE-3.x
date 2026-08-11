package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

public class GuiTerrainExtractor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nightmare:textures/gui/ifhyTerrainExtractor.png");
    private final TerrainExtractorTileEntity extractor;

    public GuiTerrainExtractor(InventoryPlayer playerInventory, TerrainExtractorTileEntity extractor) {
        super(new ContainerTerrainExtractor(playerInventory, extractor));
        this.extractor = extractor;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.getString("container.ifhyTerrainExtractor"), 8, 6, 0x404040);
        this.fontRenderer.drawString(this.extractor.getFieldName() + " "
                + String.format(Locale.ROOT, "%.3f", this.extractor.getFieldMilli() / 1000.0F), 8, 62, 0x404040);
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
