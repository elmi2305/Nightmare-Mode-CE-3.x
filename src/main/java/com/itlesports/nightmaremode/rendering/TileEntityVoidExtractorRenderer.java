package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.VoidExtractorTileEntity;
import com.itlesports.nightmaremode.rendering.models.ModelTerrainExtractor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class TileEntityVoidExtractorRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation SIDE=new ResourceLocation("nightmare:textures/blocks/ifhyExtractorAciditySide.png");
    private static final ResourceLocation TOP=new ResourceLocation("nightmare:textures/blocks/ifhyExtractorMoistureTop.png");
    private static final ResourceLocation BOTTOM=new ResourceLocation("nightmare:textures/blocks/ifhyExtractorPorosityBottom.png");
    private static final ResourceLocation STUB=new ResourceLocation("nightmare:textures/blocks/ifhyExtractorNitrogenStub.png");
    private final ModelTerrainExtractor model=new ModelTerrainExtractor();
    @Override public void renderTileEntityAt(TileEntity tile,double x,double y,double z,float partial){if(!(tile instanceof VoidExtractorTileEntity))return;GL11.glPushMatrix();GL11.glColor4f(.65F,.85F,1F,1F);GL11.glTranslated(x,y,z);GL11.glDisable(GL11.GL_CULL_FACE);int light=0xF000F0;bindTexture(SIDE);model.renderSides(light);bindTexture(TOP);model.renderTop(light);bindTexture(BOTTOM);model.renderBottom(light);bindTexture(STUB);model.renderStubs(light);GL11.glEnable(GL11.GL_CULL_FACE);GL11.glPopMatrix();GL11.glColor4f(1,1,1,1);}
}
