package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import net.minecraft.src.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// sets portal light value to 15 blocks
@Mixin(BTWMod.class)
public abstract class BTWModMixin {
    @Inject(method = "initializeHostileModeServer", at = @At("TAIL"), remap = false)
    private void changePortalLightLevel(boolean isHostile, CallbackInfo ci) {
        Block.portal.setLightValue(0.25F);
    }
}
