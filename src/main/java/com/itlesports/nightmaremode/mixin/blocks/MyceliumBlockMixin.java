package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MyceliumBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MyceliumBlock.class)
public class MyceliumBlockMixin {
    @Inject(method = "canMobsSpawnOn", at = @At("HEAD"),cancellable = true)
    private void allowMobSpawn(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
}
