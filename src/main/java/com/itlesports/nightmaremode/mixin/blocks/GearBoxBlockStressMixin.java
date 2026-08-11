package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.GearBoxBlock;
import com.itlesports.nightmaremode.mechanical.MechanicalStressManager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(GearBoxBlock.class)
public class GearBoxBlockStressMixin {
    @Inject(method = "updateTick", at = @At("TAIL"))
    private void validateMechanicalStress(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        MechanicalStressManager.validateNetwork(world, x, y, z);
    }

    @Inject(method = "onNeighborBlockChange", at = @At("TAIL"))
    private void validateMechanicalStressAfterNeighborChange(World world, int x, int y, int z,
                                                              int neighborId, CallbackInfo ci) {
        MechanicalStressManager.validateNetwork(world, x, y, z);
    }
}
