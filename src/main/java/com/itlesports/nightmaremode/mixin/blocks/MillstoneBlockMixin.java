package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MillstoneBlock;
import btw.community.nightmaremode.NightmareMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MillstoneBlock.class)
public class MillstoneBlockMixin {
    @ModifyArg(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSound(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerMillstoneVolume(float par8){
        if(NightmareMode.devMode){
            return 0.001f;
        }
        return 10;
    }
    @ModifyArg(method = "clientNotificationOfMetadataChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSound(DDDLjava/lang/String;FF)V"),index = 4)
    private float lowerMillstoneVolume1(float par8){
        if(NightmareMode.devMode){
            return 0.001f;
        }
        return 10;
    }
}
