package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(BlockLog.class)
public abstract class BlockLogMixin extends BlockRotatedPillar {
    protected BlockLogMixin(int i, Material material) {
        super(i, material);
    }

    public boolean isFallingBlock() {
        return NightmareMode.noSkybases;
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        if (NightmareMode.noSkybases) {
            this.scheduleCheckForFall(world, i, j, k);
        }
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
        if (NightmareMode.noSkybases) {
            this.scheduleCheckForFall(world, i, j, k);
        }
    }

    public void updateTick(World world, int i, int j, int k, Random rand) {
        if (NightmareMode.noSkybases) {
            this.checkForFall(world, i, j, k);
        }
    }

    public int tickRate(World par1World) {
        if (NightmareMode.noSkybases) {
            return 2;
        }
        return 10;
    }
}
