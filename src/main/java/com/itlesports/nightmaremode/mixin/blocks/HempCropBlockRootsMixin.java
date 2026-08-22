package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.BTWBlocks;
import btw.block.blocks.HempCropBlock;
import btw.block.blocks.HempCropBlockRoots;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(HempCropBlockRoots.class)
public abstract class HempCropBlockRootsMixin extends DailyGrowthCropsBlock {
    protected HempCropBlockRootsMixin(int blockID) {
        super(blockID);
    }

    @Inject(method = "attemptToGrowTop", at = @At("HEAD"), cancellable = true)
    private void growTopDuringRealTime(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        if (!NightmareMode.realTime) {
            return;
        }

        Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
        if (world.isAirBlock(x, y + 1, z) && this.getWeedsGrowthLevel(world, x, y, z) == 0 && blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z)) {
            float growthChance = this.getBaseGrowthChance(world, x, y, z) / 4.0f * blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, (Block)(Object)this);
            if (rand.nextFloat() <= growthChance) {
                world.setBlockAndMetadataWithNotify(x, y + 1, z, BTWBlocks.hempCrop.blockID, HempCropBlock.setIsTopBlock(0, true));
                blockBelow.notifyOfFullStagePlantGrowthOn(world, x, y - 1, z, (Block)(Object)this);
            }
        }
        ci.cancel();
    }
}
