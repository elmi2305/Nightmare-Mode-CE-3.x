package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// THIS CHANGES THE DIFFICULTY TO ALWAYS BE HOSTILE. this is bad for testing because in the april fools patch hostile
// disables all cheat tools. REMEMBER TO RE-ADD THIS MIXIN TO THE MIXIN CONFIG ON THE FINAL RELEASE

@Mixin(Difficulty.class)
public class DifficultyMixin {
    @Inject(method = "isHostile", at = @At("RETURN"), cancellable = true, remap = false)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
