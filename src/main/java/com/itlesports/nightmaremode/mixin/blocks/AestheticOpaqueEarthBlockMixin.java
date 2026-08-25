package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.AestheticOpaqueEarthBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(AestheticOpaqueEarthBlock.class)
public class AestheticOpaqueEarthBlockMixin {

    @Inject(method = "randomUpdateTick", at = @At("HEAD"), cancellable = true)
    private void evolveBlightFromPollution(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 0 && meta <= 5) {
            ChunkPollutionManager.tickBlight(world, x, y, z);
            ci.cancel();
        }
    }
}
