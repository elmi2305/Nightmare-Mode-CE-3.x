package com.itlesports.nightmaremode.rendering.entities;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.itlesports.nightmaremode.entity.EntityObsidianFish;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(EnvType.CLIENT)
public class RenderObsidianFish extends RenderLiving {
    private static final ResourceLocation silverfishTextures = new ResourceLocation("nightmare:textures/entity/nmObsidianFish.png");

    public RenderObsidianFish() {
        super(new ModelSilverfish(), 0.3F);
    }

    protected float getSilverfishDeathRotation(EntityObsidianFish par1EntitySilverfish) {
        return 180.0F;
    }

    public void renderSilverfish(EntityObsidianFish par1EntitySilverfish, double par2, double par4, double par6, float par8, float par9) {
        super.doRenderLiving(par1EntitySilverfish, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getSilverfishTextures(EntityObsidianFish par1EntitySilverfish) {
        return silverfishTextures;
    }

    protected int shouldSilverfishRenderPass(EntityObsidianFish par1EntitySilverfish, int par2, float par3) {
        return -1;
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.renderSilverfish((EntityObsidianFish)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.shouldSilverfishRenderPass((EntityObsidianFish)par1EntityLivingBase, par2, par3);
    }

    protected float getDeathMaxRotation(EntityLivingBase par1EntityLivingBase) {
        return this.getSilverfishDeathRotation((EntityObsidianFish)par1EntityLivingBase);
    }

    public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
        this.renderSilverfish((EntityObsidianFish)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.getSilverfishTextures((EntityObsidianFish)par1Entity);
    }

    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderSilverfish((EntityObsidianFish)par1Entity, par2, par4, par6, par8, par9);
    }
}
