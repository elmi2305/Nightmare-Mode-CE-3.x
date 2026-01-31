package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.PlayerCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerCapabilities.class)
public class PlayerCapabilitiesMixin {
    @Inject(method = "getFlySpeed", at = @At("HEAD"),cancellable = true)
    private void increaseFlySpeed(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0.1f);
    }
}
