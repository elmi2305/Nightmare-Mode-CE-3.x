package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.underworld.WorldProviderUnderworld;
import net.minecraft.src.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public class WorldProviderMixin {
    @Inject(method = "getProviderForDimension", at = @At("HEAD"),cancellable = true)
    private static void addUnderworld(int par0, CallbackInfoReturnable<WorldProvider> cir){
        if(par0 == 2){
            cir.setReturnValue(new WorldProviderUnderworld());
        }
    }
}
