package com.itlesports.nightmaremode.rendering.entities.models;

import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

/**
 * The Acid Squid's geometry is baked at its rendered size. The larger vertical
 * faces have matching UV space instead of being stretched by a render scale.
 */
public class ModelAcidSquid extends ModelBase {
    private static final float VERTICAL_SCALE = 4.2F;
    private static final float MODEL_TRANSLATE_Y = 24.125F;

    public final ModelRenderer squidBody;
    public final ModelRenderer[] squidTentacles = new ModelRenderer[8];

    public ModelAcidSquid() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.squidBody = new ModelRenderer(this, 0, 0);
        this.squidBody.addBox(-13.0F, this.scaleY(0.0F), -13.0F, 26, 67, 26);

        for (int i = 0; i < this.squidTentacles.length; ++i) {
            this.squidTentacles[i] = new ModelRenderer(this, 104, 0);
            double angle = (double)i * Math.PI * 2.0D / (double)this.squidTentacles.length;
            this.squidTentacles[i].addBox(-2.2F, 0.0F, -2.2F, 4, 76, 4);
            this.squidTentacles[i].rotationPointX = (float)Math.cos(angle) * 11.0F;
            this.squidTentacles[i].rotationPointZ = (float)Math.sin(angle) * 11.0F;
            this.squidTentacles[i].rotationPointY = this.scaleY(15.0F);
            this.squidTentacles[i].rotateAngleY = (float)(angle * -1.0D + Math.PI / 2.0D);
        }
    }

    private float scaleY(float originalY) {
        // RendererLivingEntity translates models by -24.125 pixels after
        // preRenderCallback(). Preserve the old scale-about-that-origin
        // placement while moving the scale into this model.
        return originalY * VERTICAL_SCALE + (1.0F - VERTICAL_SCALE) * MODEL_TRANSLATE_Y;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity) {
        for (ModelRenderer tentacle : this.squidTentacles) {
            tentacle.rotateAngleX = ageInTicks;
        }
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        this.squidBody.render(scale);

        int attackingTentacle = ((BTWSquidEntity)entity).tentacleAttackInProgressCounter > 0 ? 6 : -1;
        for (int i = 0; i < this.squidTentacles.length; ++i) {
            if (i != attackingTentacle) {
                this.squidTentacles[i].render(scale);
            }
        }
    }
}
