package com.itlesports.nightmaremode.rendering.entities;


import com.itlesports.nightmaremode.entity.EntityMagicArrow;
import com.itlesports.nightmaremode.entity.underworld.EntitySporeArrow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(value= EnvType.CLIENT)
public class RenderCustomArrow
        extends Render {
    private static final ResourceLocation magicTextures = new ResourceLocation("nightmare:textures/entity/magicArrow.png");
    private static final ResourceLocation sporeTextures = new ResourceLocation("nightmare:textures/entity/sporeArrow.png");

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        this.renderArrow((EntityArrow) entity, d, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity e) {
        if(e instanceof EntityMagicArrow) {
            return magicTextures;
        }
        if(e instanceof EntitySporeArrow){
            return sporeTextures;
        }
        return null;
    }

    public void renderArrow(EntityArrow entityarrow, double d, double d1, double d2, float f, float f1) {
        if (entityarrow.prevRotationYaw == 0.0f && entityarrow.prevRotationPitch == 0.0f) {
            return;
        }
        this.bindTexture(this.getEntityTexture(entityarrow));
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        GL11.glRotatef(entityarrow.prevRotationYaw + (entityarrow.rotationYaw - entityarrow.prevRotationYaw) * f1 - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(entityarrow.prevRotationPitch + (entityarrow.rotationPitch - entityarrow.prevRotationPitch) * f1, 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.instance;
        int i = 0;
        float f2 = 0.0f;
        float f3 = 0.5f;
        float f4 = (float) (0 + i * 10) / 32.0f;
        float f5 = (float) (5 + i * 10) / 32.0f;
        float f6 = 0.0f;
        float f7 = 0.15625f;
        float f8 = (float) (5 + i * 10) / 32.0f;
        float f9 = (float) (10 + i * 10) / 32.0f;
        float f10 = 0.05625f;
        GL11.glEnable(32826);
        float f11 = (float) entityarrow.arrowShake - f1;
        if (f11 > 0.0f) {
            float f12 = -MathHelper.sin(f11 * 3.0f) * f11;
            GL11.glRotatef(f12, 0.0f, 0.0f, 1.0f);
        }
        GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        GL11.glNormal3f(f10, 0.0f, 0.0f);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0, -2.0, -2.0, f6, f8);
        tessellator.addVertexWithUV(-7.0, -2.0, 2.0, f7, f8);
        tessellator.addVertexWithUV(-7.0, 2.0, 2.0, f7, f9);
        tessellator.addVertexWithUV(-7.0, 2.0, -2.0, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0f, 0.0f);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0, 2.0, -2.0, f6, f8);
        tessellator.addVertexWithUV(-7.0, 2.0, 2.0, f7, f8);
        tessellator.addVertexWithUV(-7.0, -2.0, 2.0, f7, f9);
        tessellator.addVertexWithUV(-7.0, -2.0, -2.0, f6, f9);
        tessellator.draw();
        for (int j = 0; j < 4; ++j) {
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glNormal3f(0.0f, 0.0f, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0, -2.0, 0.0, f2, f4);
            tessellator.addVertexWithUV(8.0, -2.0, 0.0, f3, f4);
            tessellator.addVertexWithUV(8.0, 2.0, 0.0, f3, f5);
            tessellator.addVertexWithUV(-8.0, 2.0, 0.0, f2, f5);
            tessellator.draw();
        }
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}