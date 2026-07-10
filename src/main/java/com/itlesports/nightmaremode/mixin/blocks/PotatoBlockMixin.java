package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.blocks.PotatoBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotatoBlock.class)
public abstract class PotatoBlockMixin extends DailyGrowthCropsBlock {
    protected PotatoBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    @ModifyArg(method = "dropBlockAsItemWithChance", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int increaseChanceOfPotato(int bound){
        return 10;
    }
}
