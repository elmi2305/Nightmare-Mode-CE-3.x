package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.EntitySlimeVariant;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import static com.itlesports.nightmaremode.util.NMFields.SLIME_HONEY;
import static com.itlesports.nightmaremode.util.NMFields.SLIME_VOID;

public class RenderSlimeVariant extends RenderLiving {

    private static final ResourceLocation HONEY = new ResourceLocation("nightmare:textures/entity/slimeHoney.png");
    private static final ResourceLocation VOID  = new ResourceLocation("nightmare:textures/entity/slimeVoid.png");

    private static final float H_R = 1.00f;
    private static final float H_G = 0.62f;
    private static final float H_B = 0.04f;

    public ModelBase scaleAmount;


    public RenderSlimeVariant(ModelBase body, ModelBase shine, float shadowSize) {
        super(body, shadowSize);
        this.scaleAmount = shine;

    }
    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.shouldRenderPassSlime((EntitySlimeVariant) par1EntityLivingBase, par2, par3);
    }


    protected int shouldRenderPassSlime(EntitySlimeVariant entitySlime, int pass, float partialTick) {
        if (entitySlime.isInvisible()) {
            return 0;
        }

        if (entitySlime.getSlimeType() == SLIME_HONEY) {
            if (pass == 0) {
                this.setRenderPassModel(this.scaleAmount);
                GL11.glBlendFunc(770, GL11.GL_ONE_MINUS_SRC_COLOR);
                GL11.glColor4f(1.0f, 0.88f, 0.30f, 1.08f);
                return 1;
            }
            if (pass == 1) {
                GL11.glDisable(3042);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            return -1;
        }


        if (pass == 0) {
                this.setRenderPassModel(this.scaleAmount);
                GL11.glEnable(2977);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                return 1;
        }
        if (pass == 1) {
                GL11.glDisable(3042);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }

        return -1;
    }

    protected void scaleSlime(EntitySlimeVariant entitySlime, float partialTick) {
        float size   = entitySlime.getSlimeSize();
        float squish = (entitySlime.prevSquishFactor
                + (entitySlime.squishFactor - entitySlime.prevSquishFactor) * partialTick)
                / (size * 0.5f + 1.0f);


        float viscous = squish * 1.6f;
        float scaleXZ = 1.0f / (viscous + 1.0f);


//        GL11.glTranslatef(0.0f, -(size * 0.04f), 0.0f);
        GL11.glScalef(scaleXZ * size, (1.0f / scaleXZ) * size, scaleXZ * size);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTick) {
        scaleSlime((EntitySlimeVariant) entity,partialTick);
        super.preRenderCallback(entity, partialTick);
        if(((EntitySlimeVariant)entity).getSlimeType() != SLIME_HONEY) return;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(H_R, H_G, H_B, 0.92f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        int slimeType = ((EntitySlimeVariant) (var1)).getSlimeType();
        if (slimeType == SLIME_HONEY) {
            return HONEY;
        } else if(slimeType == SLIME_VOID){
            return VOID;
        }

        return null;
    }
}