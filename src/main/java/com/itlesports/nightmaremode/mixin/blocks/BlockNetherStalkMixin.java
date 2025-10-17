package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockNetherStalk;
import net.minecraft.src.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockNetherStalk.class)
public class BlockNetherStalkMixin {
    @Redirect(method = "updateTick", at = @At(value = "FIELD", target = "Lnet/minecraft/src/WorldProvider;dimensionId:I"))
    private int ensureGrowsInAnyDimension(WorldProvider instance){
        return -1;
    }
}
