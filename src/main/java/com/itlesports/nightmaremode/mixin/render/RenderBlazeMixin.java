package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlaze.class)
public class RenderBlazeMixin {
    @Unique private static final ResourceLocation BLAZE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/blazeEclipse.png");
    @Unique private static final ResourceLocation BLAZE_WATER = new ResourceLocation("nightmare:textures/entity/blazeBlue.png");

    @Inject(method = "getBlazeTextures", at = @At("HEAD"),cancellable = true)
    private void blazeEclipseTextures(EntityBlaze par1, CallbackInfoReturnable<ResourceLocation> cir){
        if(NMUtils.getIsMobEclipsed(par1)){
            if(par1.isPotionActive(Potion.waterBreathing)){
                cir.setReturnValue(BLAZE_WATER);
            } else {
                cir.setReturnValue(BLAZE_ECLIPSE);
            }
        }
    }
}
