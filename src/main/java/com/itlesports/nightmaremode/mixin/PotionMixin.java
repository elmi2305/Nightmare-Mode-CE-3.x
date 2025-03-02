package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.FoodStats;
import net.minecraft.src.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Potion.class)
public class PotionMixin {
    @Shadow protected int tickEveryBase;

    @Redirect(method = "performEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;addStats(IF)V"))
    private void saturationDoesNotMakeYouFat(FoodStats foodStats, int iFoodGain, float fFatMultiplier){
        if(foodStats.getFoodLevel() < 60){
            foodStats.addStats(iFoodGain,fFatMultiplier);
        }
    }
    @Inject(method = "isReady", at = @At(value = "RETURN",ordinal = 1),cancellable = true)
    private void instantPoison(int par1, int par2, CallbackInfoReturnable<Boolean> cir){
        if(par2 >= 128){
            cir.setReturnValue(true);
        }
    }
}
