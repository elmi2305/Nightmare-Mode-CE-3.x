package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropsBlock.class)
public class CropsBlockMixin {
    @Inject(method = "canGrowAtCurrentLightLevel", at = @At("HEAD"), cancellable = true, remap = false)
    private void growDuringRealTime(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (NightmareMode.realTime) {
            cir.setReturnValue(true);
        }
    }
}
