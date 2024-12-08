package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.MillstoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MillstoneBlock.class)
public class MillstoneBlockMixin {
    @ModifyArg(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSound(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerMillstoneVolume(float par8){
        return 0.07f;
    }
    @ModifyArg(method = "clientNotificationOfMetadataChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSound(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerMillstoneVolume1(float par8){
        return 0.07f;
    }
}
