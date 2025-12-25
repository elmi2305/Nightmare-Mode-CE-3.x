package com.itlesports.nightmaremode.mixin;


import api.world.difficulty.Difficulty;
import api.world.difficulty.DifficultyParam;
import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.NMDifficultyParam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWDifficulties.class)
public class DifficultiesMixin {
    @Shadow @Final public static Difficulty HOSTILE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void editHostile(CallbackInfo ci){
        NMDifficultyParam.init();
        HOSTILE.modifyParam(DifficultyParam.ShouldStructuresBeAbandoned.class, false);
        HOSTILE.modifyParam(DifficultyParam.AbandonedStructureRangeMultiplier.class, Float.valueOf(0f));

        // make my own difficulty? no, thanks
    }
}
