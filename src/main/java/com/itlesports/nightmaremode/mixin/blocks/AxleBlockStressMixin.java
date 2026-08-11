package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.AxleBlock;
import com.itlesports.nightmaremode.mechanical.MechanicalStressManager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AxleBlock.class)
public class AxleBlockStressMixin {
    @Inject(method = "validatePowerLevel", at = @At("TAIL"))
    private void validateMechanicalStress(World world, int x, int y, int z, CallbackInfo ci) {
        MechanicalStressManager.validateNetwork(world, x, y, z);
    }
}
