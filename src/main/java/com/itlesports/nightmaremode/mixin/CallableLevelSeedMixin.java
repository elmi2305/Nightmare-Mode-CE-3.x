package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.CallableLevelSeed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CallableLevelSeed.class)
public class CallableLevelSeedMixin {
    @Redirect(method = "callLevelSeed", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean noWorldWeirdness(Difficulty instance){
        return false;
    }
}
