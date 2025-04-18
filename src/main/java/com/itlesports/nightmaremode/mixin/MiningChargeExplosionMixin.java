package com.itlesports.nightmaremode.mixin;

import btw.entity.MiningChargeExplosion;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MiningChargeExplosion.class)
public class MiningChargeExplosionMixin {
    @Shadow private World worldObj;

    @Inject(method = "destroyBlock", at = @At("HEAD"),cancellable = true,remap = false)
    private void steelCannotBeDestroyedByMiningCharges(int x, int y, int z, CallbackInfo ci){
        if(this.worldObj.getBlockId(x,y,z) == NMBlocks.steelOre.blockID && NightmareUtils.getWorldProgress(this.worldObj) < 2){
            ci.cancel();
        }
    }
}
