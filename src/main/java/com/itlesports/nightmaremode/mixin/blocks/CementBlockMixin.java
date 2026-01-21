package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.CementBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CementBlock.class)
public class CementBlockMixin {
    @Inject(method = "tickRate", at = @At("HEAD"),cancellable = true)
    private void makeCementFaster(World world, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(10);
    }
}
