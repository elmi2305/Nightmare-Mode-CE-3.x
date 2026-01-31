package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@Environment(value= EnvType.CLIENT)

public class TileEntityBloodChestRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation RES_TRAPPED = new ResourceLocation("nightmare:textures/blocks/chestBlood.png");
    private static final ResourceLocation RES_NORMAL = new ResourceLocation("nightmare:textures/blocks/chestBlood.png");

    private final ModelChest chestModel = new ModelChest();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        if (!(tileEntity instanceof TileEntityBloodChest)) {
            return;
        }
        renderChest((TileEntityBloodChest) tileEntity, x, y, z, partialTicks);
    }



    private void renderChest(TileEntityBloodChest chest, double x, double y, double z, float partialTicks) {
        // Determine lid angle
        float lidProgress = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
        lidProgress = 1.0f - lidProgress;
        lidProgress = 1.0f - lidProgress * lidProgress * lidProgress;

        this.bindTexture(RES_NORMAL);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        // Position and flip
        GL11.glTranslatef((float) x, (float) y + 1.0f, (float) z + 1.0f);
        GL11.glScalef(1f, -1f, -1f);
        GL11.glTranslatef(0.5f, 0.5f, 0.5f);

        int meta;
        if(!chest.hasWorldObj()){
            meta = 0;
        } else{
            meta = chest.getBlockMetadata();
        }
        int rotation = switch (meta) {
            case 2 -> 180;
            case 4 -> 90;
            case 5 -> -90;
            default -> 0;
        };
        GL11.glRotatef(rotation, 0f, 1f, 0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        chestModel.chestLid.rotateAngleX = -(lidProgress * (float) Math.PI / 2f);
        chestModel.renderAll();

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}