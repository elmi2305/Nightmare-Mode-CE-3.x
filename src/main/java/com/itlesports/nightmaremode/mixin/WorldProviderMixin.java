package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
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
        if(par0 == NightmareMode.UNDERWORLD_DIMENSION){
            cir.setReturnValue(new WorldProviderUnderworld());
        }
    }
}
