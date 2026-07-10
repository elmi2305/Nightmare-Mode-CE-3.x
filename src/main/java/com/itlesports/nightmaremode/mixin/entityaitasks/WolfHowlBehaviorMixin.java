package com.itlesports.nightmaremode.mixin.entityaitasks;

import btw.entity.mob.behavior.WolfHowlBehavior;
import com.itlesports.nightmaremode.util.NMUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// WOLVES PLEASE SHUT THE FUCK UP
// only tamed sitting wolves howl now, but that shouldn't be a problem

@Mixin(WolfHowlBehavior.class)
public class WolfHowlBehaviorMixin {
    @Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
    private void alwaysReturnFalse(CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(false);
    }
    @ModifyArg(method = "shouldExecute", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0),index = 0)
    private int increaseChance(int bound){
        if(NMUtils.getWorldProgress() > 0){return 4;}
        return 120;
    }
    @ModifyArg(method = "shouldExecute", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 1),index = 0)
    private int increaseChance0(int bound){
        if(NMUtils.getWorldProgress() > 0){return 4;}
        return 120;
    }
}
