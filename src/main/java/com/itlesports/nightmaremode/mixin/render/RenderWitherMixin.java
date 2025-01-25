package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.EntityBloodWither;
import net.minecraft.src.EntityWither;
import net.minecraft.src.RenderWither;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderWither.class)
public class RenderWitherMixin {
    @Unique private static final ResourceLocation BLOOD_WITHER = new ResourceLocation("textures/entity/bloodWither.png");
    @Unique private static final ResourceLocation BLOOD_WITHER_1 = new ResourceLocation("textures/entity/bloodWither1.png");
    @Unique private static final ResourceLocation BLOOD_WITHER_2 = new ResourceLocation("textures/entity/bloodWither2.png");

    @Inject(method = "func_110911_a", at = @At("HEAD"),cancellable = true)
    private void sheepEclipseTextures(EntityWither par1EntityWither, CallbackInfoReturnable<ResourceLocation> cir){
        if (par1EntityWither instanceof EntityBloodWither) {
            if (((EntityBloodWither) par1EntityWither).witherPhase == 0) {
                cir.setReturnValue(BLOOD_WITHER);
            } else if(((EntityBloodWither) par1EntityWither).witherPhase == 1){
                cir.setReturnValue(BLOOD_WITHER_1);
            } else if(((EntityBloodWither) par1EntityWither).witherPhase == 2){
                cir.setReturnValue(BLOOD_WITHER_2);
            }
        }
    }
}
