package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.EntityWalker;
import com.itlesports.nightmaremode.mixin.interfaces.RendererLivingEntityAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class RenderWalker extends RenderLiving {
    private static final ResourceLocation BODY_TEXTURE = new ResourceLocation("nightmare:textures/entity/walker_body.png");
    private static final ResourceLocation HEAD_TEXTURE = new ResourceLocation("nightmare:textures/entity/walker_head.png");

    private static final float WIDTH = 2F;
    private static final float HEIGHT = 5.5F;
    private static final float HEAD_SIZE = 2f;
    private static final float BODY_HEIGHT = HEIGHT - HEAD_SIZE;

    public RenderWalker() {
        super(new ModelZombie(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return BODY_TEXTURE;
    }

    public void renderWalkerEntity(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180F - yaw, 0F, 1F, 0F);

        renderBody();

        float headYaw = ((RendererLivingEntityAccess) this).invokeInterpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks) - yaw;
        float headPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        renderHead(headYaw, headPitch);

        GL11.glPopMatrix();
    }

    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.renderWalkerEntity((EntityWalker) par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderWalkerEntity((EntityWalker) par1Entity, par2, par4, par6, par8, par9);
    }

    private void renderBody() {
        bindTexture(BODY_TEXTURE);

        GL11.glPushMatrix();

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        drawDoubleSidedQuad(t, -WIDTH / 2F, 0F, WIDTH / 2F, BODY_HEIGHT, 0F, 0F, 1F, 1F);
        t.draw();

        GL11.glPopMatrix();
    }

    private void renderHead(float yaw, float pitch) {
        bindTexture(HEAD_TEXTURE);

        GL11.glPushMatrix();
        GL11.glTranslatef(0F, BODY_HEIGHT - 0.2f, 0F);
        GL11.glRotatef(yaw, 0F, 1F, 0F);
        GL11.glRotatef(-pitch, 1F, 0F, 0F);

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        drawDoubleSidedQuad(t, -HEAD_SIZE / 2F, 0F, HEAD_SIZE / 2F, HEAD_SIZE, 0F, 0F, 1F, 1F);
        t.draw();

        GL11.glPopMatrix();
    }

    private void drawDoubleSidedQuad(Tessellator t, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        t.addVertexWithUV(x0, y0, 0, u0, v1);
        t.addVertexWithUV(x1, y0, 0, u1, v1);
        t.addVertexWithUV(x1, y1, 0, u1, v0);
        t.addVertexWithUV(x0, y1, 0, u0, v0);

        t.addVertexWithUV(x0, y1, 0, u0, v0);
        t.addVertexWithUV(x1, y1, 0, u1, v0);
        t.addVertexWithUV(x1, y0, 0, u1, v1);
        t.addVertexWithUV(x0, y0, 0, u0, v1);
    }
}