package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
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
    @Unique private static final ResourceLocation FIRE_SKELETON_TEXTURE = new ResourceLocation("textures/entity/fireskeleton.png");
    @Unique private static final ResourceLocation ICE_SKELETON_TEXTURE = new ResourceLocation("textures/entity/iceskeleton.png");
    @Unique private static final ResourceLocation ENDER_SKELETON_TEXTURE = new ResourceLocation("textures/entity/enderskeleton.png");

    @Unique private static final ResourceLocation FIRE_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/fireskeletonEclipse.png");
    @Unique private static final ResourceLocation ICE_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/iceskeletonEclipse.png");
    @Unique private static final ResourceLocation ENDER_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/enderskeletonEclipse.png");
    @Unique private static final ResourceLocation WITHER_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/witherskeletonEclipse.png");
    @Unique private static final ResourceLocation NORMAL_SKELETON_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/normalskeletonEclipse.png");



    @Inject(method = "func_110860_a", at = @At("HEAD"), cancellable = true)
    private void manageVariantTextures(EntitySkeleton skeleton, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!NightmareUtils.getIsMobEclipsed(skeleton)) {
            if(skeleton.getSkeletonType() == 4){
                cir.setReturnValue(ENDER_SKELETON_TEXTURE);
            }else if(skeleton.getSkeletonType() == 3){
                cir.setReturnValue(FIRE_SKELETON_TEXTURE);
            } else if(skeleton.getSkeletonType() == 2){
                cir.setReturnValue(ICE_SKELETON_TEXTURE);
            }
        } else{
            if(skeleton.getSkeletonType() == 4) {
                cir.setReturnValue(ENDER_SKELETON_TEXTURE_ECLIPSE);
            }else if(skeleton.getSkeletonType() == 3){
                cir.setReturnValue(FIRE_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType() == 2){
                cir.setReturnValue(ICE_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType() == 1){
                cir.setReturnValue(WITHER_SKELETON_TEXTURE_ECLIPSE);
            } else if(skeleton.getSkeletonType() == 0){
                cir.setReturnValue(NORMAL_SKELETON_TEXTURE_ECLIPSE);
            }
        }
    }
}
