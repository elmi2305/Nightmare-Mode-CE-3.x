package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.RenderWitch;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderWitch.class)
public class RenderWitchMixin {
    @Unique private static final ResourceLocation WITCH_ECLIPSE = new ResourceLocation("nightmare:textures/entity/witchEclipse.png");

    @Inject(method = "getWitchTextures", at = @At("HEAD"),cancellable = true)
    private void eclipseWitchTextures(EntityWitch par1EntityWitch, CallbackInfoReturnable<ResourceLocation> cir){
        if(NMUtils.getIsMobEclipsed(par1EntityWitch)){
            cir.setReturnValue(WITCH_ECLIPSE);
        }
    }
}
