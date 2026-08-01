package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.BlockRedstoneOre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRedstoneOre.class)
public class BlockRedstoneOreMixin {
    @Inject(method = "idDropped", at = @At("HEAD"), cancellable = true)
    private void dropRedstoneCrystal(int metadata, Random random, int fortune,
                                     CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.redstoneCrystal.itemID);
    }

    @Inject(method = "quantityDropped", at = @At("HEAD"), cancellable = true)
    private void dropOneRedstoneCrystal(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }

    @Inject(method = "quantityDroppedWithBonus", at = @At("HEAD"), cancellable = true)
    private void ignoreFortuneForRedstoneCrystal(int fortune, Random random,
                                                 CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
}
