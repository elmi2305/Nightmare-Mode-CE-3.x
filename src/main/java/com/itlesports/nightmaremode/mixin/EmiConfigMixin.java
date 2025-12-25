package com.itlesports.nightmaremode.mixin;

import emi.dev.emi.emi.config.ConfigPresets;
import emi.dev.emi.emi.config.EmiConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmiConfig.class)
public class EmiConfigMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void manageDefaultConfig(CallbackInfo ci){
        ConfigPresets.oldDefault.run();
    }
}
