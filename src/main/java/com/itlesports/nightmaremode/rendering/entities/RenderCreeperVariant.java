package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import static com.itlesports.nightmaremode.util.NMFields.*;

public class RenderCreeperVariant extends RenderLiving {
    public RenderCreeperVariant() {
        super(new ModelCreeper(), 0.5f);
    }

    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");

    private static final ResourceLocation FIRE_CREEPER_TEXTURE = new ResourceLocation("nightmare:textures/entity/firecreeper.png");
    private static final ResourceLocation FIRE_CREEPER_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/firecreeperEclipseHigh.png");
    private static final ResourceLocation FIRE_CREEPER_TEXTURE_CHARGED = new ResourceLocation("nightmare:textures/entity/firecreeperCharged.png");

    private static final ResourceLocation DUNG_CREEPER_TEXTURE = new ResourceLocation("nightmare:textures/entity/creeperDung.png");

    private static final ResourceLocation LIGHTNING_CREEPER_TEXTURE = new ResourceLocation("nightmare:textures/entity/lightningCreeper.png");

    public static final ResourceLocation SUPER_CREEPER_TEXTURE = new ResourceLocation("nightmare:textures/entity/creeperSupercritical.png");

    private static final ResourceLocation OBSIDIAN_CREEPER_TEXTURE = new ResourceLocation("nightmare:textures/entity/nmObsidianCreeper.png");


    private ModelBase creeperModel = new ModelCreeper(2.0f);

    protected void updateCreeperScale(EntityCreeperVariant mob, float par2) {
        float flashIntensity = mob.getCreeperFlashIntensity(par2);
        float var4 = 1.0f + MathHelper.sin(flashIntensity * 100.0f) * flashIntensity * 0.01f;
        if (flashIntensity < 0.0f) {
            flashIntensity = 0.0f;
        }
        if (flashIntensity > 1.0f) {
            flashIntensity = 1.0f;
        }
        flashIntensity *= flashIntensity;
        flashIntensity *= flashIntensity;
        float var5 = (1.0f + flashIntensity * 0.4f) * var4;
        float var6 = (1.0f + flashIntensity * 0.1f) / var4;
        GL11.glScalef(var5, var6, var5);
    }

    protected int updateCreeperColorMultiplier(EntityCreeperVariant c, float par2, float par3) {
        float flashIntensity = c.getCreeperFlashIntensity(par3);
        if ((int)(flashIntensity * 10.0f) % 2 == 0) {
            return 0;
        }
        int var5 = (int)(flashIntensity * 0.2f * 255.0f);
        if (var5 < 0) {
            var5 = 0;
        }
        if (var5 > 255) {
            var5 = 255;
        }
        int var6 = 255;
        int var7 = 255;
        int var8 = 255;
        return var5 << 24 | var6 << 16 | var7 << 8 | var8;
    }

    protected int renderCreeperPassModel(EntityCreeperVariant c, int par2, float par3) {
        if (c.getPowered()) {
            if (c.isInvisible()) {
                GL11.glDepthMask(false);
            } else {
                GL11.glDepthMask(true);
            }
            if (par2 == 1) {
                float var4 = (float)c.ticksExisted + par3;
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

    protected int func_77061_b(EntityCreeperVariant par1EntityCreeper, int par2, float par3) {
        return -1;
    }

    protected ResourceLocation getCreeperTextures(EntityCreeperVariant entity) {
        if(entity.variantType == CREEPER_FIRE){
            if(NMUtils.getIsMobEclipsed(entity)){
                return FIRE_CREEPER_TEXTURE_ECLIPSE;
            } else if(entity.isCharged()){
                return FIRE_CREEPER_TEXTURE_CHARGED;
            }
            return FIRE_CREEPER_TEXTURE;
        }
        else if(entity.variantType == CREEPER_DUNG){
            return DUNG_CREEPER_TEXTURE;
        }
        else if(entity.variantType == CREEPER_LIGHTNING){
            return LIGHTNING_CREEPER_TEXTURE;
        }
        else if(entity.variantType == CREEPER_OBSIDIAN){
            return OBSIDIAN_CREEPER_TEXTURE;
        }
        else if(entity.variantType == CREEPER_SUPERCRITICAL){
            return SUPER_CREEPER_TEXTURE;
        }
        return creeperTextures;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float par2) {
        this.updateCreeperScale((EntityCreeperVariant) entity, par2);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entity, float par2, float par3) {
        return this.updateCreeperColorMultiplier((EntityCreeperVariant)entity, par2, par3);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int par2, float par3) {
        return this.renderCreeperPassModel((EntityCreeperVariant)entity, par2, par3);
    }

    @Override
    protected int inheritRenderPass(EntityLivingBase entity, int par2, float par3) {
        return this.func_77061_b((EntityCreeperVariant)entity, par2, par3);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getCreeperTextures((EntityCreeperVariant) entity);
    }
}
