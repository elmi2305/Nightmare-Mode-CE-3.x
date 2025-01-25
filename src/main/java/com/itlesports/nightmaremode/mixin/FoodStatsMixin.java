package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void startingFoodLevel(CallbackInfo ci){
        if(NightmareMode.perfectStart){
            FoodStats thisObj = (FoodStats)(Object)this;
            thisObj.setFoodLevel(36);
        }
    }
}
