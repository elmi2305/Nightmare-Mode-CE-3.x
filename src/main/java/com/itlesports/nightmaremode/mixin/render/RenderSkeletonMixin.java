package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMUtils;
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
    @Unique private static final ResourceLocation FIRE_SKELETON_TEXTURE = new ResourceLocation("nightmare:textures/entity/skeletonFire.png");
    @Unique private static final ResourceLocation ICE_SKELETON_TEXTURE = new ResourceLocation("nightmare:textures/entity/skeletonIce.png");
    @Unique private static final ResourceLocation ENDER_SKELETON_TEXTURE = new ResourceLocation("nightmare:textures/entity/skeletonEnder.png");
    @Unique private static final ResourceLocation JUNGLE_SKELETON_TEXTURE = new ResourceLocation("nightmare:textures/entity/skeletonJungle.png");
    @Unique private static final ResourceLocation SUPERCRITICAL_SKELETON_TEXTURE = new ResourceLocation("nightmare:textures/entity/skeletonSupercritical.png");

    @Unique private static final ResourceLocation FIRE_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/fireskeletonEclipse.png");
    @Unique private static final ResourceLocation ICE_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/iceskeletonEclipse.png");
    @Unique private static final ResourceLocation ENDER_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/enderskeletonEclipse.png");
    @Unique private static final ResourceLocation WITHER_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/witherskeletonEclipse.png");
    @Unique private static final ResourceLocation NORMAL_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/normalskeletonEclipse.png");



    @Inject(method = "func_110860_a", at = @At("HEAD"), cancellable = true)
    private void manageVariantTextures(EntitySkeleton skeleton, CallbackInfoReturnable<ResourceLocation> cir) {
        if(skeleton.getSkeletonType().id() == NightmareMode.SKELETON_JUNGLE){
            cir.setReturnValue(JUNGLE_SKELETON_TEXTURE);
        } else if(skeleton.getSkeletonType().id() == NightmareMode.SKELETON_SUPERCRITICAL) {
            cir.setReturnValue(SUPERCRITICAL_SKELETON_TEXTURE);
        }
        if (!NMUtils.getIsMobEclipsed(skeleton)) {
             if(skeleton.getSkeletonType().id() == 4){
                cir.setReturnValue(ENDER_SKELETON_TEXTURE);
            }else if(skeleton.getSkeletonType().id() == 3){
                cir.setReturnValue(FIRE_SKELETON_TEXTURE);
            } else if(skeleton.getSkeletonType().id() == 2){
                cir.setReturnValue(ICE_SKELETON_TEXTURE);
            }
        } else{
            if(skeleton.getSkeletonType().id() == 4) {
                cir.setReturnValue(ENDER_SKELETON_TEXTURE_ECLIPSE);
            }else if(skeleton.getSkeletonType().id() == 3){
                cir.setReturnValue(FIRE_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType().id() == 2){
                cir.setReturnValue(ICE_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType().id() == 1){
                cir.setReturnValue(WITHER_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType().id() == 0){
                cir.setReturnValue(NORMAL_SKELETON_TEXTURE_ECLIPSE);
            }
        }
    }
}
