package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.CrystalTorchBlock;
import com.itlesports.nightmaremode.block.tileEntities.CrystalTorchTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CrystalTorchRenderer extends TileEntitySpecialRenderer {
    private final RenderBlocks renderer = new RenderBlocks();

    @Override public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof CrystalTorchTileEntity torch) || tile.worldObj == null
                || tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord) != NMBlocks.submergedCrystalTorch.blockID) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            this.bindTexture(TextureMap.locationBlocksTexture);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setBrightness(NMBlocks.submergedCrystalTorch.getMixedBrightnessForBlock(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord));
            tessellator.setColorOpaque_F(1, 1, 1);
            CrystalTorchBlock.renderTorch(this.renderer, NMBlocks.crystalTorch, 0, 0, 0, torch.getOrientation(), 0);
            tessellator.draw();
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
