package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.EnderChestBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin {
    @Inject(method = "computeLevelOfEnderChestsAntenna", at = @At("RETURN"),cancellable = true)
    private void enderChestsAlwaysActive(World world, int i, int j, int k, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(4);
        // makes ender chests behave normally
    }
}
