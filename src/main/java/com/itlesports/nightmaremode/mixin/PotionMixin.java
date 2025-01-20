package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.FoodStats;
import net.minecraft.src.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Potion.class)
public class PotionMixin {
    @Redirect(method = "performEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;addStats(IF)V"))
    private void saturationDoesNotMakeYouFat(FoodStats foodStats, int iFoodGain, float fFatMultiplier){
        if(foodStats.getFoodLevel() < 60){
            foodStats.addStats(iFoodGain,fFatMultiplier);
        }
    }
}
