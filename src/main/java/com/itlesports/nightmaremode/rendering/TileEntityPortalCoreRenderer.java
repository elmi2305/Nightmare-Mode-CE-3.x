package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import com.itlesports.nightmaremode.util.underworld.RitualState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

/**
 * Renders the PortalCore block with dynamic textures based on activity state,
 * and draws the vertical energy beam during an active ritual.
 */
@Environment(value = EnvType.CLIENT)
public class TileEntityPortalCoreRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation TEXTURE_SIDE = new ResourceLocation("nightmare:textures/blocks/nmPortalCoreSide.png");
    private static final ResourceLocation TEXTURE_TOP = new ResourceLocation("nightmare:textures/blocks/nmPortalCoreTop.png");
    private static final ResourceLocation TEXTURE_TOP_FILLED = new ResourceLocation("nightmare:textures/blocks/nmPortalCoreTopFilled.png");
    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("nightmare:textures/effects/red.png");


    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileEntityPortalCore core)) return;

        renderBlock(core, x, y, z);

        if (core.getState() != RitualState.ACTIVE) {
            if (core.beamHeight <= 0f) return;
        } else {
            renderBeam(core, x, y, z);
        }
    }

    private void renderBlock(TileEntityPortalCore core, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LIGHTING);
        Block block = core.getBlockType();

        int brightness = block.getMixedBrightnessForBlock(core.worldObj, core.xCoord, core.yCoord + 1, core.zCoord);
        // render sides using side texture
        this.doTessellateStuff(brightness, (byte) 1, core);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
    private void doTessellateStuff(int brightness, byte meta, TileEntityPortalCore tile) {
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
//        t.setColorRGBA_F(1.0f, 1.0f, 1.0f, 1.0f);
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
                return TEXTURE_TOP_FILLED;
            }
            return TEXTURE_TOP;
        }
        return TEXTURE_SIDE;
    }

    private void renderBeam(TileEntityPortalCore core, double x, double y, double z) {
        float height = 13f;

        long  worldTime = core.worldObj != null ? core.worldObj.getTotalWorldTime() : 0;
        float scrollV = (worldTime % 80) / 80f;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.0, z + 0.5);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // additive glows
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);

        this.bindTexture(BEAM_TEXTURE);

        // pass 1 inner bright column two crossing planes fast rotation amplified
        renderBeamPass(worldTime, height, scrollV,  0.15f, 0.35f, 0.85f, 0.95f, 2.0f);

        // pass 2 halo counter rotating slightly wider amplified
        renderBeamPass(worldTime, height, scrollV + 0.25f, 0.20f, 0.40f, 0.70f, 0.75f, -1.2f);

        // pass 3 outer soft glow wide very transparent amplified
        GL11.glColor4f(0.45f, 0.15f, 0.85f, 0.25f);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        drawBeamPlane(t, 0.85f, 0f, height, scrollV, 0.8f);
        t.draw();
        GL11.glRotatef(90f, 0f, 1f, 0f);
        t.startDrawingQuads();
        drawBeamPlane(t, 0.85f, 0f, height, scrollV, 0.8f);
        t.draw();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPopMatrix();
    }

    /**
     * renders one rotation pass of the beam two crossed quads at a given width alpha
     * rotating at rotspeed degrees per worldtime unit
     */
    private void renderBeamPass(long worldTime, float height, float scrollV,
                                float r, float g, float b, float alpha,
                                float rotSpeed) {
        float rot = (worldTime * rotSpeed) % 360f;
        Tessellator t = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glRotatef(rot, 0f, 1f, 0f);
        GL11.glColor4f(r, g, b, alpha);

        // first plane along x axis increased width
        t.startDrawingQuads();
        drawBeamPlane(t, 0.20f, 0f, height, scrollV, 1.0f);
        t.draw();

        // second plane along z axis 90 rotated increased width
        GL11.glRotatef(90f, 0f, 1f, 0f);
        t.startDrawingQuads();
        drawBeamPlane(t, 0.20f, 0f, height, scrollV, 1.0f);
        t.draw();

        GL11.glPopMatrix();
    }

    /**
     * helper method that draws a single vertical quad centred on the current origin
     */
    private void drawBeamPlane(Tessellator t, float halfWidth, float yBottom, float yTop, float vBottom, float uvRepeat) {
        float vTop = vBottom + uvRepeat;
        t.addVertexWithUV(-halfWidth, yBottom,0,0.0, vBottom);
        t.addVertexWithUV( halfWidth, yBottom,0,1.0, vBottom);
        t.addVertexWithUV( halfWidth, yTop,0,1.0, vTop);
        t.addVertexWithUV(-halfWidth, yTop,0,0.0, vTop);
    }
}