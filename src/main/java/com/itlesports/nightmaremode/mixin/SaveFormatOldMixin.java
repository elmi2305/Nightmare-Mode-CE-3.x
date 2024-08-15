package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.SaveFormatOld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaveFormatOld.class)
public class SaveFormatOldMixin {
    @Inject(method = "isWorldHostile", at = @At("RETURN"),cancellable = true,remap = false)
    private void noWorldNonsense(String worldName, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
