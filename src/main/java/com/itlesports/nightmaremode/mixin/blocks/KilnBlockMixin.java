package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.KilnBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KilnBlock.class)
public abstract class KilnBlockMixin {
    @ModifyConstant(method = "updateTick", constant = @Constant(intValue = 15))
    private int reduceCookTime(int constant) {
        return constant;
    }

    @Inject(method = "getBlockCookTimeMultiplier", at = @At("RETURN"), cancellable = true)
    private void increaseCookingSpeed(IBlockAccess blockAccess, int i, int j, int k, CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        returnValue *= 2 + NMUtils.getWorldProgress() * 2;
        int blockID = blockAccess.getBlockId(i, j, k);
        if (blockID == BTWBlocks.unfiredPottery.blockID) {
            returnValue *= 2;
        }
        cir.setReturnValue(returnValue);
    }

    @Inject(method = "cookBlock", at = @At("HEAD"))
    private void recordKilnedIronNuggets(World world, int x, int y, int z, CallbackInfo ci) {
        if (world == null || world.isRemote) {
            return;
        }
        Block input = Block.blocksList[world.getBlockId(x, y, z)];
        if (input == null) {
            return;
        }
        ItemStack[] outputs = input.getOutputsWhenCookedByKiln(world, x, y, z);
        int ironNuggets = 0;
        if (outputs != null) {
            for (ItemStack output : outputs) {
                if (output != null && output.itemID == BTWItems.ironNugget.itemID) {
                    ironNuggets += output.stackSize;
                }
            }
        }
        if (ironNuggets <= 0) {
            return;
        }
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 16.0D);
        SkillHandler.incrementIronNuggetsKilned(player, ironNuggets);
    }
}
