package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGhast.class)
public class RenderGhastMixin {
    @Unique private static final ResourceLocation GHAST_ECLIPSE = new ResourceLocation("textures/entity/ghastEclipse.png");
    @Unique private static final ResourceLocation GHAST_CREEPER = new ResourceLocation("textures/entity/ghastCreeper.png");

    @Inject(method = "func_110867_a", at = @At("HEAD"),cancellable = true)
    private void eclipseTextures(EntityGhast par1, CallbackInfoReturnable<ResourceLocation> cir) {
        if (NMUtils.getIsMobEclipsed(par1)) {
            if (par1.isPotionActive(Potion.moveSpeed)) {
                cir.setReturnValue(GHAST_CREEPER);
            } else {
                cir.setReturnValue(GHAST_ECLIPSE);
            }
        }
    }
}
