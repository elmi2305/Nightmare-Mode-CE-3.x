package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.UnfiredBrickTileEntity;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(UnfiredBrickTileEntity.class)
public class UnfiredBrickTileEntityMixin {
    @ModifyConstant(method = "updateCooking", constant = @Constant(intValue = 11900),remap = false)
    private int reduceClayCookTime(int constant){
        return 11400;
    }

    @Redirect(method = "updateCooking", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockNaturalLightValueMaximum(III)I"))
    private int enableCookingOnEclipse(World instance, int i, int j, int k){
        if(NMUtils.getIsEclipse()){
            return 31;
        }
        return instance.getBlockNaturalLightValueMaximum(i,j,k);
    }
}
