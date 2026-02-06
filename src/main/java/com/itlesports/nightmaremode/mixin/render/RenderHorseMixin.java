package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.RenderHorse;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderHorse.class)
public class RenderHorseMixin {
    @Unique private static final ResourceLocation HORSE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/horseEclipse.png");

    @Inject(method = "func_110849_a", at = @At("HEAD"),cancellable = true)
    private void horseEclipseTextures(EntityHorse par1, CallbackInfoReturnable<ResourceLocation> cir){
        if (NMUtils.getIsMobEclipsed(par1)) {
            cir.setReturnValue(HORSE_ECLIPSE);
        }
    }
}
