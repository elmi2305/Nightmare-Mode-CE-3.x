package com.itlesports.nightmaremode.rendering;


import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import com.itlesports.nightmaremode.util.underworld.RitualState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

/**
 * Draws the vertical energy beam during an active ritual.
 *
 * The beam is built from two pairs of crossed, rotating quads with scrolling UVs
 * and additive blending so it looks like a column of light/dark energy.
 * An outer glow quad layer gives it volume.
 */
@Environment(value = EnvType.CLIENT)
public class TileEntityPortalCoreRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation BEAM_TEXTURE =
            new ResourceLocation("nightmare:textures/effects/ritualBeam.png");


    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TileEntityPortalCore core)) return;

        if (core.getState() != RitualState.ACTIVE) {
            if (core.beamHeight <= 0f) return;
        }

        renderBeam(core, x, y, z);
    }


    private void renderBeam(TileEntityPortalCore core, double x, double y, double z) {
        float height = core.beamHeight;
        if (height <= 0f) return;

        long  worldTime = core.worldObj != null ? core.worldObj.getTotalWorldTime() : 0;
        float scrollV = (worldTime % 80) / 80f;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.0, z + 0.5);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // additive — glows
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);

        this.bindTexture(BEAM_TEXTURE);

        // Pass 1: inner bright column (two crossing planes, fast rotation)
        renderBeamPass(worldTime, height, scrollV,  0.06f, 0.18f, 0.55f, 0.85f, 1.5f);

        // Pass 2: halo (counter-rotating, slightly wider)
        renderBeamPass(worldTime, height, scrollV + 0.25f, 0.10f, 0.20f, 0.40f, 0.50f, -0.8f);

        // Pass 3: outer soft glow (wide, very transparent)
        GL11.glColor4f(0.25f, 0.0f, 0.65f, 0.08f);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        drawBeamPlane(t, 0.55f, 0f, height, scrollV, 0.6f);
        t.draw();
        GL11.glRotatef(90f, 0f, 1f, 0f);
        t.startDrawingQuads();
        drawBeamPlane(t, 0.55f, 0f, height, scrollV, 0.6f);
        t.draw();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPopMatrix();
    }

    /**
     * Renders one rotation-pass of the beam: two crossed quads at a given width/alpha,
     * rotating at rotSpeed degrees per worldTime unit.
     */
    private void renderBeamPass(long worldTime, float height, float scrollV,
                                float r, float g, float b, float alpha,
                                float rotSpeed) {
        float rot = (worldTime * rotSpeed) % 360f;
        Tessellator t = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glRotatef(rot, 0f, 1f, 0f);
        GL11.glColor4f(r, g, b, alpha);

        // First plane (along X axis)
        t.startDrawingQuads();
        drawBeamPlane(t, 0.08f, 0f, height, scrollV, 1.0f);
        t.draw();

        // Second plane (along Z axis — 90° rotated)
        GL11.glRotatef(90f, 0f, 1f, 0f);
        t.startDrawingQuads();
        drawBeamPlane(t, 0.08f, 0f, height, scrollV, 1.0f);
        t.draw();

        GL11.glPopMatrix();
    }

    /**
     * Helper method that draws a single vertical quad centred on the current origin.
     */
    private void drawBeamPlane(Tessellator t, float halfWidth, float yBottom, float yTop, float vBottom, float uvRepeat) {
        float vTop = vBottom + uvRepeat;
        t.addVertexWithUV(-halfWidth, yBottom,0,0.0, vBottom);
        t.addVertexWithUV( halfWidth, yBottom,0,1.0, vBottom);
        t.addVertexWithUV( halfWidth, yTop,0,1.0, vTop);
        t.addVertexWithUV(-halfWidth, yTop,0,0.0, vTop);
    }
}