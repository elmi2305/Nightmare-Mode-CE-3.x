package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.RenderSkeleton;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSkeleton.class)
public class RenderSkeletonMixin {
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
