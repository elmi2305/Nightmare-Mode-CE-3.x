package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.entity.underworld.EntityAwakenedWither;
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
    @Unique private static final ResourceLocation BLOOD = new ResourceLocation("nightmare:textures/entity/bloodWither.png");
    @Unique private static final ResourceLocation AWAKENED = new ResourceLocation("nightmare:textures/entity/awakenedWither.png");

    @Inject(method = "func_110911_a", at = @At("HEAD"),cancellable = true)
    private void sheepEclipseTextures(EntityWither wither, CallbackInfoReturnable<ResourceLocation> cir){
        if (wither instanceof EntityBloodWither) {
            cir.setReturnValue(BLOOD);
        }
        if (wither instanceof EntityAwakenedWither) {
            cir.setReturnValue(AWAKENED);
        }
    }
}
