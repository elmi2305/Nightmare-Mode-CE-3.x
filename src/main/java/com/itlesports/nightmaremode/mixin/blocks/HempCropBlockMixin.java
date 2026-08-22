package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.HempCropBlock;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.BlockCrops;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(HempCropBlock.class)
public abstract class HempCropBlockMixin extends BlockCrops {
    protected HempCropBlockMixin(int blockID) {
        super(blockID);
    }

    @Shadow protected abstract boolean getIsTopBlock(int metadata);

    @Inject(method = "getBaseGrowthChance", at = @At("HEAD"),cancellable = true)
    private void makeHempGrowFaster(World world, int i, int j, int k, CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0.2f);
    }

    @Inject(method = "attemptToGrow", at = @At("HEAD"), cancellable = true)
    private void growDuringRealTime(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        if (!NightmareMode.realTime) {
            return;
        }

        int metadata = world.getBlockMetadata(x, y, z);
        Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
        if (!this.getIsTopBlock(metadata) && this.getWeedsGrowthLevel(world, x, y, z) == 0 && blockBelow != null && blockBelow.isBlockHydratedForPlantGrowthOn(world, x, y - 1, z)) {
            if (this.getGrowthLevel(world, x, y, z) < 7) {
                float growthChance = this.getBaseGrowthChance(world, x, y, z) * blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, (Block)(Object)this);
                if (rand.nextFloat() <= growthChance) {
                    this.incrementGrowthLevel(world, x, y, z);
                }
            } else if (world.isAirBlock(x, y + 1, z)) {
                float growthChance = this.getBaseGrowthChance(world, x, y, z) / 4.0f * blockBelow.getPlantGrowthOnMultiplier(world, x, y - 1, z, (Block)(Object)this);
                if (rand.nextFloat() <= growthChance) {
                    world.setBlockAndMetadataWithNotify(x, y + 1, z, this.blockID, HempCropBlock.setIsTopBlock(0, true));
                    blockBelow.notifyOfFullStagePlantGrowthOn(world, x, y - 1, z, (Block)(Object)this);
                }
            }
        }
        ci.cancel();
    }
}
