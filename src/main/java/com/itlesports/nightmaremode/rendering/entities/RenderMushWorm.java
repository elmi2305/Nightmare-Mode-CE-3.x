package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.EntityMushWorm;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(EnvType.CLIENT)
public class RenderMushWorm extends RenderLiving {
    private static final ResourceLocation mushWormTextures = new ResourceLocation("nightmare:textures/entity/nmMushWorm.png");

    public RenderMushWorm() {
        super(new ModelSilverfish(), 0.3F);
    }

    protected float getMushWormDeathRotation(EntityMushWorm par1EntityMushWorm) {
        return 180.0F;
    }

    public void renderMushWorm(EntityMushWorm par1EntityMushWorm, double par2, double par4, double par6, float par8, float par9) {
        super.doRenderLiving(par1EntityMushWorm, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getMushWormTextures(EntityMushWorm par1EntityMushWorm) {
        return mushWormTextures;
    }

    protected int shouldMushWormRenderPass(EntityMushWorm par1EntityMushWorm, int par2, float par3) {
        return -1;
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.renderMushWorm((EntityMushWorm)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.shouldMushWormRenderPass((EntityMushWorm)par1EntityLivingBase, par2, par3);
    }

    protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
        return this.getMushWormDeathRotation((EntityMushWorm)par1EntityLivingBase);
    }

    public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
        this.renderMushWorm((EntityMushWorm)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.getMushWormTextures((EntityMushWorm)par1Entity);
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderMushWorm((EntityMushWorm)par1Entity, par2, par4, par6, par8, par9);
    }
}