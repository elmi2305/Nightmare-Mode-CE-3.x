package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.KilnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(KilnBlock.class)
public class KilnBlockMixin {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 15))
    private int reduceCookTime(int constant){
        return 6;
    }
}
