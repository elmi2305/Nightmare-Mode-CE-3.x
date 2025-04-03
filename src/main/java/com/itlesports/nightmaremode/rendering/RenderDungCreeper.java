package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.entity.EntityDungCreeper;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderDungCreeper extends RenderCreeper {

    private final ModelBase creeperModel = new ModelCreeper(2.0f);
    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    public static final ResourceLocation DUNG_CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeperDung.png");

    public RenderDungCreeper() {
        super();
    }
    @Override
    protected ResourceLocation getCreeperTextures(EntityCreeper par1EntityCreeper) {
        if (par1EntityCreeper instanceof EntityDungCreeper) {
            return DUNG_CREEPER_TEXTURE;
        } else {return super.getEntityTexture(par1EntityCreeper);}
    }
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        if(par1Entity instanceof EntityDungCreeper) {
            return DUNG_CREEPER_TEXTURE;
        } else {
            return super.getEntityTexture(par1Entity);
        }
    }


    protected int renderCreeperPassModel(EntityDungCreeper par1EntityCreeper, int par2, float par3) {
        if (par1EntityCreeper.getPowered()) {
            GL11.glDepthMask(!par1EntityCreeper.isInvisible());
            if (par2 == 1) {
                float var4 = (float)par1EntityCreeper.ticksExisted + par3;
                this.bindTexture(armoredCreeperTextures);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                float var5 = var4 * 0.01f;
                float var6 = var4 * 0.01f;
                GL11.glTranslatef(var5, var6, 0.0f);
                this.setRenderPassModel(this.creeperModel);
                GL11.glMatrixMode(5888);
                GL11.glEnable(3042);
                float var7 = 0.5f;
                GL11.glColor4f(var7, var7, var7, 1.0f);
                GL11.glDisable(2896);
                GL11.glBlendFunc(1, 1);
                return 1;
            }
            if (par2 == 2) {
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(5888);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
            }
        }
        return -1;
    }
    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.renderCreeperPassModel((EntityDungCreeper)par1EntityLivingBase, par2, par3);
    }
}
