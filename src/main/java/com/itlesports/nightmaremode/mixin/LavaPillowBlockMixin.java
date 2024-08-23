package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.LavaPillowBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LavaPillowBlock.class)
public class LavaPillowBlockMixin {
    @Redirect(method = "setBlockToLava", at = @At(value = "INVOKE", target = "Lbtw/block/blocks/LavaPillowBlock;hasWaterToSidesOrTop(Lnet/minecraft/src/World;III)Z"))
    private boolean unforgivingLavaPillows(LavaPillowBlock instance, World world, int i, int j, int k){
        return false;
    }
}
