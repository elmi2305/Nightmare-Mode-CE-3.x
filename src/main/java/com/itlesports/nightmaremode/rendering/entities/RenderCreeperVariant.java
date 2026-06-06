package com.itlesports.nightmaremode.rendering.entities;

import btw.client.render.util.RenderUtils;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.rendering.entities.models.ModelDungCreeper;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import static com.itlesports.nightmaremode.util.NMFields.*;

public class RenderCreeperVariant extends RenderLiving {
    public RenderCreeperVariant() {
        super(new ModelCreeper(), 0.5f);
        this.mainModel = creeperModel[0];
    }

    private static final int MODEL_BASE = 0;
    private static final int MODEL_ARMOR = 1;

    private static final ResourceLocation armoredCreeperTextures = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private static final ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");

    private static final ResourceLocation FIRE = new ResourceLocation("nightmare:textures/entity/firecreeper.png");
    private static final ResourceLocation FIRE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/firecreeperEclipseHigh.png");
    private static final ResourceLocation FIRE_CHARGED = new ResourceLocation("nightmare:textures/entity/firecreeperCharged.png");

    private static final ResourceLocation DUNG = new ResourceLocation("nightmare:textures/entity/creeperDung.png");
    private static final ResourceLocation DUNG_OLD = new ResourceLocation("nightmare:textures/entity/creeperDungOld.png");

    private static final ResourceLocation LIGHTNING = new ResourceLocation("nightmare:textures/entity/lightningCreeper.png");

    public static final ResourceLocation NITRO = new ResourceLocation("nightmare:textures/entity/creeperSupercritical.png");

    private static final ResourceLocation OBSIDIAN = new ResourceLocation("nightmare:textures/entity/nmObsidianCreeper.png");

    private static final ResourceLocation FLOWER = new ResourceLocation("nightmare:textures/entity/nmFlowerCreeper.png");

    private static final ResourceLocation VOID = new ResourceLocation("nightmare:textures/entity/creeperVoid.png");

    private static final ResourceLocation GEL = new ResourceLocation("nightmare:textures/entity/creeperGel.png");

    private static final ResourceLocation GLITCH = new ResourceLocation("nightmare:textures/entity/creeperGlitch.png");

    private final ModelBase[] creeperModel = {new ModelCreeper(0), new ModelCreeper(2)};
    private final ModelDungCreeper[] creeperDungModel = {new ModelDungCreeper(0), new ModelDungCreeper(2)};

    protected void updateCreeperScale(EntityCreeperVariant mob, float par2) {
        float flashIntensity = mob.getCreeperFlashIntensity(par2);

        if (mob.variantType == PACKET_CREEPER_GLITCH) {
            flashIntensity *= (float)(1 + Math.sin(mob.worldObj.getWorldTime() * 0.2) * 0.5);
        }

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

        if (mob.variantType == PACKET_CREEPER_GLITCH) {
            long time = mob.worldObj.getWorldTime() + mob.entityId * 31L;

            var5 *= (float)(1 + Math.sin(time * 0.2) * 0.02);
            var6 *= (float)(1 + Math.sin(time * 0.17) * 0.02);

            float squashX = (float)(1.0 + Math.sin(time * 0.13) * 0.05);
            float squashY = (float)(1.0 + Math.cos(time * 0.11) * 0.05);
            float squashZ = (float)(1.0 + Math.sin(time * 0.19) * 0.05);

            GL11.glScalef(squashX, squashY, squashZ);

            if ((time / 3) % 7 == 0) {
                GL11.glRotatef((time * 37) % 360, 0.0F, 1.0F, 0.0F);
            }

            if ((time / 5) % 13 == 0) {
                GL11.glRotatef((time * 61) % 360, 1.0F, 0.0F, 0.0F);
            }

            if ((time / 7) % 11 == 0) {
                GL11.glRotatef((time * 89) % 360, 0.0F, 0.0F, 1.0F);
            }

            if ((time / 2) % 13 == 0) {
                GL11.glTranslatef(
                        (float)Math.sin(time * 1.7) * 0.3F,
                        (float)Math.cos(time * 2.1) * 0.3F,
                        (float)Math.sin(time * 1.3) * 0.3F
                );
            }

            if ((time / 11) % 31 == 0) {
                float mirror = ((time / 11) & 1) == 0 ? -1.0F : 1.0F;
                GL11.glScalef(mirror, 1.0F, 1.0F);
            }
        }

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

    protected void renderCreeper(EntityCreeperVariant c, double par2, double par4, double par6, float par8, float par9) {
        this.mainModel = getCreeperModels(c)[MODEL_BASE];
        super.doRenderLiving(c, par2, par4, par6, par8, par9);
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
                this.setRenderPassModel(getCreeperModels(c)[MODEL_ARMOR]);
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
        boolean usingLegacy = RenderUtils.shouldRenderLegacyModel();

        if(entity.variantType == PACKET_CREEPER_FIRE){
            if(NMUtils.getIsMobEclipsed(entity)){
                return FIRE_ECLIPSE;
            } else if(entity.isCharged()){
                return FIRE_CHARGED;
            }
            return FIRE;
        }
        else if(entity.variantType == PACKET_CREEPER_DUNG){
            return usingLegacy ? DUNG_OLD : DUNG;
        }
        else if(entity.variantType == PACKET_CREEPER_LIGHTNING){
            return LIGHTNING;
        }
        else if(entity.variantType == PACKET_CREEPER_OBSIDIAN){
            return OBSIDIAN;
        }
        else if(entity.variantType == PACKET_CREEPER_SUPERCRITICAL){
            return NITRO;
        }
        else if(entity.variantType == PACKET_CREEPER_FLOWER){
            return FLOWER;
        }
        else if(entity.variantType == PACKET_CREEPER_VOID){
            return VOID;
        }
        else if(entity.variantType == PACKET_CREEPER_GEL){
            return GEL;
        }
        else if(entity.variantType == PACKET_CREEPER_GLITCH){
            return GLITCH;
        }
        return creeperTextures;
    }

    /**
     * Returns a model array that is equivalent to the provided entity
     * @param entity Creeper entity
     * @return An array of models
     */
    protected ModelBase[] getCreeperModels(EntityCreeperVariant entity)
    {
        // If we're using the legacy render flag, only use
        // base creeper model
        if (RenderUtils.shouldRenderLegacyModel()) {
            return creeperModel;
        }

        return switch (entity.variantType) {
            case PACKET_CREEPER_DUNG -> creeperDungModel;
            default -> creeperModel;
        };
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

    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
        this.renderCreeper((EntityCreeperVariant) entity, par2, par4, par6, par8, par9);
    }
}
