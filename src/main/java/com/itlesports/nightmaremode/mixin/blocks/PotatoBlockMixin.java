package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.PotatoBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PotatoBlock.class)
public class PotatoBlockMixin {
    @ModifyArg(method = "dropBlockAsItemWithChance", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int increaseChanceOfPotato(int bound){
        return 2;
    }
}
