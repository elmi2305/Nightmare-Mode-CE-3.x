package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.SawBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SawBlock.class)
public class SawBlockMixin {
    @ModifyArg(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSoundEffect(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerSawVolume(float par8){
        return 0.25f;
    }
    @ModifyArg(method = "scheduleUpdateIfRequired", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSoundEffect(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerSawVolumeAgain(float par8){
        return 0.25f;
    }

}
