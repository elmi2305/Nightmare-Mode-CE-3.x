package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.rendering.entities.models.ModelRift;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderUnderworldPortal extends RendererLivingEntity {
    private static final ResourceLocation PORTAL_TEXTURE = new ResourceLocation("nightmare:textures/entity/nmPortalUnderworldComplete.png");
    private static final ResourceLocation PORTAL_BORDER = new ResourceLocation("nightmare:textures/entity/nmPortalUnderworldBorder.png");

    public RenderUnderworldPortal() {
        super(new ModelRift(), 0.5f);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float)x, (float)y + 2, (float)z);

        GL11.glScalef(1.5f,2f,1.5f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);


        // face camera
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);


        Tessellator tess = Tessellator.instance;

        float size = 2.0F;

        GL11.glDisable(GL11.GL_LIGHTING);

        bindTexture(PORTAL_TEXTURE);

        tess.startDrawingQuads();

        tess.addVertexWithUV(-size, -size, 0, 0, 1);
        tess.addVertexWithUV( size, -size, 0, 1, 1);
        tess.addVertexWithUV( size,  size, 0, 1, 0);
        tess.addVertexWithUV(-size,  size, 0, 0, 0);

        tess.draw();

        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
//        super.doRender(entity, x, y, z, yaw, partialTicks);
    }
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return PORTAL_TEXTURE;
    }
}
