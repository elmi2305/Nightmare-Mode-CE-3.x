package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.blocks.BloodBoneBlock;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodBone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class TileEntityBloodBoneRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation texture = new ResourceLocation("nightmare:textures/blocks/nmBloodBone.png");
    private static final ResourceLocation hole = new ResourceLocation("nightmare:textures/blocks/nmBloodBoneHole.png");
    private static final ResourceLocation star = new ResourceLocation("nightmare:textures/blocks/nmBloodBoneStar.png");
    private final Random rand = new Random();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        if (!(tileEntity instanceof TileEntityBloodBone)) {
            return;
        }
        renderBlock((TileEntityBloodBone) tileEntity, x, y, z);
    }

    private void renderBlock(TileEntityBloodBone tile, double x, double y, double z) {
        if (tile == null || tile.worldObj == null || tile.getBlockType() == null) {
            return;
        }

        Block block = tile.getBlockType();
        boolean isAngry = tile.isAngry();
        byte meta = ((BloodBoneBlock) block).getNetherStarSide();

        float rotX = tile.xRot;
        float rotY = tile.yRot;
        float rotZ = tile.zRot;

        float twitchAmount = tile.getTwitchAmount();
        if (twitchAmount > 0) {
            rotX += (rand.nextFloat() - 0.5F) * twitchAmount * 40F;
            rotY += (rand.nextFloat() - 0.5F) * twitchAmount * 40F;
            rotZ += (rand.nextFloat() - 0.5F) * twitchAmount * 40F;
        }

        int brightness = block.getMixedBrightnessForBlock(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);

        GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.bindTexture(texture);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (isAngry) {
            GL11.glColor4f(0.2F, 1.0F, 3.0F, 1.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        doTessellateStuff(brightness, meta, tile);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void doTessellateStuff(int brightness, byte meta, TileEntityBloodBone tile) {
        Tessellator t = Tessellator.instance;
        boolean active = tile.isActive();

        renderFace(t, brightness, getTexture(meta, (byte) 0, active),
                0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1,
                0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 1.0);

        renderFace(t, brightness, getTexture(meta, (byte) 1, active),
                0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0,
                0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0);

        renderFace(t, brightness, getTexture(meta, (byte) 2, active),
                0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0,
                0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0);

        renderFace(t, brightness, getTexture(meta, (byte) 3, active),
                1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1,
                1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);

        renderFace(t, brightness, getTexture(meta, (byte) 4, active),
                0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0,
                1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0);

        renderFace(t, brightness, getTexture(meta, (byte) 5, active),
                1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1,
                0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0);
    }

    private void renderFace(Tessellator t, int brightness, ResourceLocation faceTexture,
                            double x1, double y1, double z1,
                            double x2, double y2, double z2,
                            double x3, double y3, double z3,
                            double x4, double y4, double z4,
                            double u1, double v1,
                            double u2, double v2,
                            double u3, double v3,
                            double u4, double v4) {
        t.startDrawingQuads();
        t.setBrightness(brightness);
        this.bindTexture(faceTexture);
        t.addVertexWithUV(x1, y1, z1, u1, v1);
        t.addVertexWithUV(x2, y2, z2, u2, v2);
        t.addVertexWithUV(x3, y3, z3, u3, v3);
        t.addVertexWithUV(x4, y4, z4, u4, v4);
        t.draw();
    }

    private ResourceLocation getTexture(byte meta, byte side, boolean isActive) {
        if (meta == side) {
            if (isActive) {
                return star;
            }
            return hole;
        }
        return texture;
    }
}