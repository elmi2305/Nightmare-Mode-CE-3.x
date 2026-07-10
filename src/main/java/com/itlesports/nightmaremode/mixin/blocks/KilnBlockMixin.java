package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.KilnBlock;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KilnBlock.class)
public class KilnBlockMixin {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 15))
    private int reduceCookTime(int constant){
        return constant;
    }

    @Inject(method = "getBlockCookTimeMultiplier", at = @At("RETURN"),cancellable = true)
    private void increaseCookingSpeed(IBlockAccess blockAccess, int i, int j, int k, CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        returnValue *= 2 + NMUtils.getWorldProgress() * 2;
        int blockID = blockAccess.getBlockId(i, j, k);
        if(blockID == BTWBlocks.unfiredPottery.blockID){
            returnValue *= 2;
        }
        cir.setReturnValue(returnValue);
    }
}
