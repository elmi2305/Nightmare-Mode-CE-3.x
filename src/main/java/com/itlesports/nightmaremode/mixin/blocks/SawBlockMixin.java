package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.SawBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SawBlock.class)
public class SawBlockMixin {

    @ModifyArg(method = "scheduleUpdateIfRequired", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;scheduleBlockUpdate(IIIII)V", ordinal = 1), index = 4)
    private int lowerSawCuttingTime(int par1){
        return 120;
    }
}
