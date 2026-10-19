package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.DailyGrowthCropsBlock;
import btw.block.blocks.PotatoBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.PotatoHarvestRates;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotatoBlock.class)
public abstract class PotatoBlockMixin extends DailyGrowthCropsBlock {
    protected PotatoBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"), cancellable = true)
    private void scalePotatoHarvest(World world, int x, int y, int z, int metadata,
                                    float chance, int fortune, CallbackInfo ci) {
        if (!world.isRemote && this.isFullyGrown(metadata)) {
            super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, 0);
            int extra = PotatoHarvestRates.extraPotatoes(
                    ChunkAttributeManager.get(world, x, z).get(ChunkAttribute.POTASSIUM), world.rand);
            for (int roll = 0; roll < extra; ++roll) {
                super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, 0);
            }
        }
        ci.cancel();
    }
}
