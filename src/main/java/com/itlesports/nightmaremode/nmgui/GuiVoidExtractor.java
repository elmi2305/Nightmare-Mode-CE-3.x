package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.VoidExtractorTileEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class GuiVoidExtractor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nightmare:textures/gui/ifhyVoidExtractor.png");
    public GuiVoidExtractor(InventoryPlayer inventory,VoidExtractorTileEntity extractor){super(new ContainerVoidExtractor(inventory,extractor));this.ySize=166;}
    @Override protected void drawGuiContainerForegroundLayer(int x,int y){fontRenderer.drawString(I18n.getString("container.ifhyVoidExtractor"),8,6,0x404040);fontRenderer.drawString(I18n.getString("container.inventory"),8,72,0x404040);}
    @Override protected void drawGuiContainerBackgroundLayer(float partial,int mx,int my){GL11.glColor4f(1,1,1,1);mc.getTextureManager().bindTexture(TEXTURE);int x=(width-xSize)/2,y=(height-ySize)/2;drawTexturedModalRect(x,y,0,0,xSize,ySize);}
}
