package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockMushroom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BlockMushroom.class)
public class BlockMushroomMixin {
    @ModifyArg(method = "updateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 1))
    private int increaseChanceToSpread(int bound){
        return 6;
    }
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 4,ordinal = 0))
    private int decreaseMushroomDetectionRange(int constant){
        return 2;
    }
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 4,ordinal = 1))
    private int increaseAttemptsToSpread(int constant){
        return 7;
    }
}
