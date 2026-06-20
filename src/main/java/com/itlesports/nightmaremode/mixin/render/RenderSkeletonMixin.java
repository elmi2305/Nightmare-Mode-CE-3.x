package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSkeleton.class)
public class RenderSkeletonMixin extends RenderBiped {
    @Unique private static final ResourceLocation FIRE = new ResourceLocation("nightmare:textures/entity/skeletonFire.png");
    @Unique private static final ResourceLocation ICE = new ResourceLocation("nightmare:textures/entity/skeletonIce.png");
    @Unique private static final ResourceLocation ENDER = new ResourceLocation("nightmare:textures/entity/skeletonEnder.png");
    @Unique private static final ResourceLocation JUNGLE = new ResourceLocation("nightmare:textures/entity/skeletonJungle.png");
    @Unique private static final ResourceLocation NITRO = new ResourceLocation("nightmare:textures/entity/skeletonSupercritical.png");
    @Unique private static final ResourceLocation LIGHTNING = new ResourceLocation("nightmare:textures/entity/skeletonLightning.png");

    @Unique private static final ResourceLocation FIRE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/fireskeletonEclipse.png");
    @Unique private static final ResourceLocation ICE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/iceskeletonEclipse.png");
    @Unique private static final ResourceLocation ENDER_ECLIPSE = new ResourceLocation("nightmare:textures/entity/enderskeletonEclipse.png");
    @Unique private static final ResourceLocation WITHER_ECLIPSE = new ResourceLocation("nightmare:textures/entity/witherskeletonEclipse.png");
    @Unique private static final ResourceLocation NORMAL_ECLIPSE = new ResourceLocation("nightmare:textures/entity/normalskeletonEclipse.png");

    @Unique private static final ResourceLocation armor = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    public RenderSkeletonMixin(ModelBiped par1ModelBiped, float par2) {
        super(par1ModelBiped, par2);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        if (((EntitySkeleton)entity).getSkeletonType().id() == NMFields.SKELETON_LIGHTNING) {
            return this.renderSkeletonPassModel((EntitySkeleton) entity, pass, partialTicks);
        }

        return super.shouldRenderPass(entity, pass, partialTicks);
    }


    @Unique protected int renderSkeletonPassModel(EntitySkeleton c, int par2, float partialTicks) {
        GL11.glDepthMask(!c.isInvisible());

        if (par2 == 1) {
            float var4 = (float)c.ticksExisted + partialTicks;
            this.bindTexture(armor);
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            float var5 = var4 * 0.01f;
            float var6 = var4 * 0.01f;
            GL11.glTranslatef(var5, var6, 0.0f);
            this.setRenderPassModel(this.mainModel);
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
        return -1;
    }

    @Inject(method = "func_110860_a", at = @At("HEAD"), cancellable = true)
    private void manageVariantTextures(EntitySkeleton skeleton, CallbackInfoReturnable<ResourceLocation> cir) {
        int id = skeleton.getSkeletonType().id();

        if(id == NMFields.SKELETON_JUNGLE){
            cir.setReturnValue(JUNGLE);
        } else if(id == NMFields.SKELETON_SUPERCRITICAL) {
            cir.setReturnValue(NITRO);
        } else if(id == NMFields.SKELETON_LIGHTNING) {
            cir.setReturnValue(LIGHTNING);
        }


        if (NMUtils.getIsMobEclipsed(skeleton)) {
            if(id == NMFields.SKELETON_ENDER) {
                cir.setReturnValue(ENDER_ECLIPSE);
            }else if(id == NMFields.SKELETON_FIRE) {
                cir.setReturnValue(FIRE_ECLIPSE);
            } else if(id == NMFields.SKELETON_ICE){
                cir.setReturnValue(ICE_ECLIPSE);
            } else if(id == NMFields.SKELETON_WITHER) {
                cir.setReturnValue(WITHER_ECLIPSE);
            } else if(id == 0){
                cir.setReturnValue(NORMAL_ECLIPSE);
            }
        } else {
             if(id == NMFields.SKELETON_ENDER){
                cir.setReturnValue(ENDER);
            }else if(id == NMFields.SKELETON_FIRE){
                cir.setReturnValue(FIRE);
            } else if(id == NMFields.SKELETON_ICE){
                cir.setReturnValue(ICE);
            }
        }
    }
}
