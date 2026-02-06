package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.UnfiredBrickTileEntity;
import btw.community.nightmaremode.NightmareMode;
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
    private int enableCookingOnEclipse(World w, int i, int j, int k){
        if(NMUtils.getIsEclipse()){
            return 31;
        }
        if(NightmareMode.darkStormyNightmare){
            long time = w.getWorldTime() % 24000;
            if(time > 0 && time < 13000){
                return 31;
            }
        }
        return w.getBlockNaturalLightValueMaximum(i,j,k);
    }
}
